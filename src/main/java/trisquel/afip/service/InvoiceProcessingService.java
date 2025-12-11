package trisquel.afip.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import trisquel.afip.AfipSoapRequestBuilder;
import trisquel.afip.config.AfipURLs;
import trisquel.afip.model.AfipAuth;
import trisquel.afip.model.AfipComprobante;
import trisquel.afip.model.ErrorType;
import trisquel.model.*;
import trisquel.repository.InvoiceQueueRepository;
import trisquel.service.ConfigurationService;
import trisquel.service.InvoiceService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceProcessingService.class);
    private final InvoiceQueueRepository invoiceQueueRepository;
    private final InvoiceService invoiceService;
    private final WsaaService wsaaService;
    private final AfipResponseInterpreterService responseInterpreterService;
    private final RestTemplate restTemplate;
    private final ConfigurationService configurationService;
    private static final int STUCK_THRESHOLD_MINUTES = 10;

    public InvoiceProcessingService(InvoiceQueueRepository invoiceQueueRepository, InvoiceService invoiceService,
                                    RestTemplate restTemplate,
                                    AfipResponseInterpreterService afipResponseInterpreterService,
                                    WsaaService wsaaService, ConfigurationService configurationService) {
        this.invoiceQueueRepository = invoiceQueueRepository;
        this.invoiceService = invoiceService;
        this.wsaaService = wsaaService;
        this.responseInterpreterService = afipResponseInterpreterService;
        this.restTemplate = restTemplate;
        this.configurationService = configurationService;
    }

    @Async
    @Scheduled(fixedRate = 1800000) // every 30min
    public void processQueuedInvoices() {
        logger.info("Running scheduled invoice processing");

        // Validar configuración de organización
        Optional<ConfigurationMap> orgInfo = configurationService.findByKey("org");
        if (orgInfo.isEmpty()) {
            logger.error("No org info found, skipping invoice processing");
            return;
        }

        String cuit = orgInfo.get().getValueAsString("cuit");
        if (cuit == null || cuit.isEmpty()) {
            logger.error("CUIT not found, skipping invoice processing");
            return;
        }

        // Buscar facturas listas para procesar (incluyendo trabadas)
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime stuckThreshold = now.minusMinutes(STUCK_THRESHOLD_MINUTES);

        List<InvoiceQueue> invoicesToProcess = invoiceQueueRepository.findReadyToProcess(Arrays.asList(InvoiceQueueStatus.QUEUED, InvoiceQueueStatus.BEING_PROCESSED), now, stuckThreshold);

        if (invoicesToProcess.isEmpty()) {
            logger.info("No invoices to process");
            return;
        }

        logger.info("Processing {} invoices", invoicesToProcess.size());

        // Procesar cada factura en su propia transacción
        for (InvoiceQueue invoiceQueue : invoicesToProcess) {
            try {
                processInvoiceInTransaction(invoiceQueue, cuit);
            } catch (Exception e) {
                logger.error("Unexpected error processing invoice queue ID {}: {}", invoiceQueue.getId(), e.getMessage(), e);
            }
        }

        logger.info("Finished processing invoices");
    }

    @Transactional
    public void processInvoiceInTransaction(InvoiceQueue invoiceQueue, String cuit) {
        logger.info("Processing invoice queue ID: {}, invoice ID: {}, retry count: {}", invoiceQueue.getId(), invoiceQueue.getInvoiceId(), invoiceQueue.getRetryCount());

        // Obtener la factura
        Optional<Invoice> invoiceOpt = invoiceService.findFullInvoiceById(invoiceQueue.getInvoiceId());
        if (invoiceOpt.isEmpty()) {
            logger.error("Invoice ID {} not found for queue ID {}", invoiceQueue.getInvoiceId(), invoiceQueue.getId());
            markAsTotalFailure(invoiceQueue, null, ErrorType.UNKNOWN, "Invoice not found in database");
            return;
        }

        Invoice invoice = invoiceOpt.get();

        // Marcar como en procesamiento
        invoiceQueue.markAsBeingProcessed();
        invoiceQueueRepository.save(invoiceQueue);

        try {
            processInvoice(invoiceQueue, invoice, cuit);
        } catch (Exception e) {
            logger.error("Error processing invoice queue ID {}: {}", invoiceQueue.getId(), e.getMessage(), e);
            handleProcessingError(invoiceQueue, invoice, e);
        }
    }

    private void processInvoice(InvoiceQueue invoiceQueue, Invoice invoice, String cuit) throws Exception {
        // Autenticar con AFIP
        AfipAuth afipAuth = wsaaService.autenticar();
        if (afipAuth.getToken() == null && afipAuth.getErrorMessage() != null) {
            throw new RuntimeException("Fallo en la autenticacion contra AFIP: " + afipAuth.getErrorMessage());
        }

        // Obtener datos necesarios
        Client client = invoice.getClient();
        InvoiceIvaBreakdown invoiceBreakdown = new InvoiceIvaBreakdown(invoice);
        Long lastAuthorizedComprobanteNumber = getLastAuthorizedComprobante(afipAuth, cuit, invoice.getSellPoint(), invoice.getComprobante());

        // Construir y enviar request
        String request = AfipSoapRequestBuilder.buildFECAESolicitarRequest(afipAuth, cuit, invoice, client, invoiceBreakdown, lastAuthorizedComprobanteNumber);
        invoiceQueue.setRequest(request);

        String responseBody = invokeFECAEWithRestTemplate(request);
        invoiceQueue.setResponse(responseBody);

        // Interpretar respuesta
        AfipResponseInterpreterService.CaeResponse interpretedResponse = responseInterpreterService.parseFecaeFromResponse(responseBody);

        handleInvoiceQueueResponseProcess(invoiceQueue, interpretedResponse, invoice, lastAuthorizedComprobanteNumber);
    }

    @Transactional
    public void handleInvoiceQueueResponseProcess(InvoiceQueue invoiceQueue,
                                                  AfipResponseInterpreterService.CaeResponse interpretedResponse,
                                                  Invoice invoice, Long lastAuthorizedComprobanteNumber) {
        // Completar información del queue con la respuesta
        interpretedResponse.completeInvoiceQueue(invoiceQueue);
        invoiceQueue.setProcessedAt(ZonedDateTime.now());

        if (interpretedResponse.isExitoso()) {
            // Procesamiento exitoso
            logger.info("Invoice queue ID {} processed successfully with CAE: {}", invoiceQueue.getId(), interpretedResponse.getCae());

            invoiceQueue.setStatus(InvoiceQueueStatus.COMPLETED);
            invoiceQueueRepository.save(invoiceQueue);

            invoiceService.updateAfipFields(invoice, interpretedResponse.getCae(), interpretedResponse.getFechaVencimientoCae());
            invoiceService.updateInvoiceStatus(invoice, InvoiceQueueStatus.COMPLETED);
            invoiceService.updateInvoiceNumber(invoice, lastAuthorizedComprobanteNumber);

        } else {
            // Procesamiento falló
            logger.warn("Invoice queue ID {} failed. Errors: {}, Observations: {}", invoiceQueue.getId(), invoiceQueue.getErrors(), invoiceQueue.getObservations());

            handleReprocessInvoiceQueue(invoiceQueue, invoice, ErrorType.AFIP_VALIDATION_ERROR);
        }
    }

    private void handleProcessingError(InvoiceQueue invoiceQueue, Invoice invoice, Exception e) {
        // TODO: En el futuro, clasificar errores según el tipo de excepción y mensaje
        // Por ejemplo:
        // - SocketTimeoutException -> ErrorType.AFIP_TIMEOUT
        // - HttpClientErrorException con 4xx -> ErrorType.AFIP_VALIDATION_ERROR
        // - HttpServerErrorException con 5xx -> ErrorType.AFIP_SERVER_ERROR
        // - etc.

        ErrorType errorType = classifyError(e);
        String errorDetails = getStackTraceAsString(e);

        handleReprocessInvoiceQueue(invoiceQueue, invoice, errorType, errorDetails);
    }

    private ErrorType classifyError(Exception e) {
        // TODO: Implementar clasificación más sofisticada según los errores reales de AFIP
        // Por ahora, reintentar todo
        String message = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

        if (message.contains("timeout") || message.contains("timed out")) {
            return ErrorType.AFIP_TIMEOUT;
        } else if (message.contains("autenticacion")) {
            return ErrorType.AUTHENTICATION_ERROR;
        } else if (message.contains("network") || message.contains("connection")) {
            return ErrorType.NETWORK_ERROR;
        }

        return ErrorType.UNKNOWN;
    }

    private void handleReprocessInvoiceQueue(InvoiceQueue invoiceQueue, Invoice invoice, ErrorType errorType) {
        handleReprocessInvoiceQueue(invoiceQueue, invoice, errorType, null);
    }

    private void handleReprocessInvoiceQueue(InvoiceQueue invoiceQueue, Invoice invoice, ErrorType errorType,
                                             String errorDetails) {
        invoiceQueue.setStatus(InvoiceQueueStatus.FAILED);
        invoiceQueue.setErrorType(errorType);
        if (errorDetails != null) {
            invoiceQueue.setErrorDetails(errorDetails);
        }
        invoiceQueueRepository.save(invoiceQueue);

        // Intentar crear un reintento
        InvoiceQueue retryQueue = InvoiceQueue.createRetryFromFailedQueue(invoiceQueue);

        if (retryQueue != null) {
            logger.info("Creating retry {} for invoice queue ID {}, next retry at: {}", retryQueue.getRetryCount(), invoiceQueue.getId(), retryQueue.getNextRetryAt());

            invoiceQueueRepository.save(retryQueue);
        } else {
            logger.error("Invoice queue ID {} exhausted all retries, marking as TOTAL_FAILURE", invoiceQueue.getId());

            markAsTotalFailure(invoiceQueue, invoice, errorType, "Exceeded maximum retry attempts (3)");
        }
    }

    private void markAsTotalFailure(InvoiceQueue invoiceQueue, Invoice invoice, ErrorType errorType,
                                    String errorDetails) {
        invoiceQueue.setStatus(InvoiceQueueStatus.TOTAL_FAILURE);
        invoiceQueue.setErrorType(errorType);
        if (errorDetails != null && invoiceQueue.getErrorDetails() == null) {
            invoiceQueue.setErrorDetails(errorDetails);
        }
        invoiceQueue.setProcessedAt(ZonedDateTime.now());
        invoiceQueueRepository.save(invoiceQueue);

        if (invoice != null) {
            invoiceService.updateInvoiceStatus(invoice, InvoiceQueueStatus.TOTAL_FAILURE);
        }
    }

    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    private String invokeFECAEWithRestTemplate(String soapRequest) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("SOAPAction", AfipURLs.fecaeSolicitarAction);
        headers.set("Content-Type", "text/xml; charset=utf-8");

        HttpEntity<String> requestEntity = new HttpEntity<>(soapRequest, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(AfipURLs.fecaeUrl, HttpMethod.POST, requestEntity, String.class);
            return response.getBody();
        } catch (HttpServerErrorException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Long getLastAuthorizedComprobante(AfipAuth afipAuth, String cuit, Long sellPoint,
                                             AfipComprobante comprobante) {
        String request = AfipSoapRequestBuilder.buildFECompUltimoAutorizadoRequest(afipAuth, cuit, sellPoint, comprobante);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("SOAPAction", AfipURLs.FECompUltimoAutorizadoAction);
        headers.set("Content-Type", "text/xml; charset=utf-8");

        HttpEntity<String> requestEntity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(AfipURLs.ultCompAuthUrl, HttpMethod.POST, requestEntity, String.class);
            String responseBody = response.getBody();
            Long lastAuthorizedComprobanteNumber = AfipResponseInterpreterService.getNumberFECompUltimoAutorizado(responseBody);
            return lastAuthorizedComprobanteNumber;
        } catch (HttpServerErrorException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
