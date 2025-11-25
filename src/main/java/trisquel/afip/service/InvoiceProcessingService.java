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
import trisquel.model.*;
import trisquel.repository.InvoiceQueueRepository;
import trisquel.service.ConfigurationService;
import trisquel.service.InvoiceService;

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
    @Scheduled(fixedRate = 900000) // every 15min
    public void processQueuedInvoices() {
        logger.info("Running scheduled invoice processing");
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
        List<InvoiceQueue> invoicesToProcess = invoiceQueueRepository.findByStatusInOrderByEnqueuedAtAsc(Arrays.asList(InvoiceQueueStatus.QUEUED, InvoiceQueueStatus.RETRY));
        if (invoicesToProcess.isEmpty()) {
            logger.debug("No invoices to process");
            return;
        }
        logger.info("Processing {} invoices", invoicesToProcess.size());
        for (InvoiceQueue invoiceQueue : invoicesToProcess) {
            Optional<Invoice> invoice = invoiceService.findFullInvoiceById(invoiceQueue.getInvoiceId());
            try {
                invoiceQueue.setStatus(InvoiceQueueStatus.BEING_PROCESSED);
                invoiceQueueRepository.save(invoiceQueue);
                processInvoice(invoiceQueue, invoice.get(), cuit);
            } catch (Exception e) {
                logger.error("Error processing invoice queue ID {}: {}", invoiceQueue.getId(), e.getMessage(), e);
                handleReprocessInvoiceQueue(invoiceQueue, invoice.get());
            }
        }
        logger.info("Finished processing invoices");
    }

    private void processInvoice(InvoiceQueue invoiceQueue, Invoice invoice, String cuit) throws Exception {
        AfipAuth afipAuth = wsaaService.autenticar();
        if (afipAuth.getToken() == null && afipAuth.getErrorMessage() != null) {
            throw new RuntimeException("Fallo en la autenticacion contra AFIP: " + afipAuth.getErrorMessage());
        }
        Client client = invoice.getClient();
        InvoiceIvaBreakdown invoiceBreakdown = new InvoiceIvaBreakdown(invoice);
        Long lastAuthorizedComprobanteNumber = getLastAuthorizedComprobante(afipAuth, cuit, invoice.getSellPoint(), invoice.getComprobante());
        String request = AfipSoapRequestBuilder.buildFECAESolicitarRequest(afipAuth, cuit, invoice, client, invoiceBreakdown, lastAuthorizedComprobanteNumber);
        invoiceQueue.setRequest(request);
        String responseBody = invokeFECAEWithRestTemplate(request);
        invoiceQueue.setResponse(responseBody);
        AfipResponseInterpreterService.CaeResponse interpretedResponse = responseInterpreterService.parseFecaeFromResponse(responseBody);
        handleInvoiceQueueResponseProcess(invoiceQueue, interpretedResponse, invoice, lastAuthorizedComprobanteNumber);
    }

    @Transactional
    public void handleInvoiceQueueResponseProcess(InvoiceQueue invoiceQueue,
                                                  AfipResponseInterpreterService.CaeResponse interpretedResponse,
                                                  Invoice invoice, Long lastAuthorizedComprobanteNumber) {
        interpretedResponse.completeInvoiceQueue(invoiceQueue);
        invoiceQueue.setProcessedAt(ZonedDateTime.now());
        // Handle invoice confirmed
        if (interpretedResponse.isExitoso()) {
            invoiceQueue.setStatus(InvoiceQueueStatus.COMPLETED);
            invoiceQueueRepository.save(invoiceQueue);
            invoiceService.updateAfipFields(invoice, interpretedResponse.getCae(), interpretedResponse.getFechaVencimientoCae());
            invoiceService.updateInvoiceStatus(invoice, InvoiceQueueStatus.COMPLETED);
            invoiceService.updateInvoiceNumber(invoice, lastAuthorizedComprobanteNumber);
        } else {
            // Handle invoice that needs reprocess or failed for some reason. I think there may be cases where timeouts, etc.
            handleReprocessInvoiceQueue(invoiceQueue, invoice);
        }
    }

    private void handleReprocessInvoiceQueue(InvoiceQueue invoiceQueue, Invoice invoice) {
        invoiceQueue.setStatus(InvoiceQueueStatus.FAILED);
        invoiceQueue.incrementRetryCount();
        invoiceQueueRepository.save(invoiceQueue);
        Optional<InvoiceQueue> reprocessedInvoiceQueue = InvoiceQueue.createInvoiceQueueFromUncompletedInvoiceQueue(invoiceQueue);
        if (reprocessedInvoiceQueue.isPresent()) {
            // Enqueue new Invoice request
            invoiceQueueRepository.save(reprocessedInvoiceQueue.get());
        } else {
            invoiceQueue.setStatus(InvoiceQueueStatus.TOTAL_FAILURE);
            invoiceQueueRepository.save(invoiceQueue);
            invoiceService.updateInvoiceStatus(invoice, InvoiceQueueStatus.TOTAL_FAILURE);
        }
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
