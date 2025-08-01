package trisquel.afip.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import trisquel.afip.AfipSoapRequestBuilder;
import trisquel.afip.config.AfipConfiguration;
import trisquel.afip.model.AfipAuth;
import trisquel.model.*;
import trisquel.repository.InvoiceQueueRepository;
import trisquel.service.InvoiceService;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceProcessingService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceProcessingService.class);
    private final InvoiceQueueRepository invoiceQueueRepository;
    private final AfipInvoiceService afipInvoiceService;
    private final InvoiceService invoiceService;
    private final WsaaService wsaaService;
    private final AfipResponseInterpreterService responseInterpreterService;
    private final RestTemplate restTemplate;

    private final String trisquelCUIT = "30717409775";
    private final String fecaeUrl = "https://wswhomo.afip.gov.ar/wsfev1/service.asmx";

    public InvoiceProcessingService(InvoiceQueueRepository invoiceQueueRepository,
                                    AfipInvoiceService afipInvoiceService, AfipConfiguration afipConfig,
                                    InvoiceService invoiceService, RestTemplate restTemplate,
                                    AfipResponseInterpreterService afipResponseInterpreterService,
                                    WsaaService wsaaService) {
        this.invoiceQueueRepository = invoiceQueueRepository;
        this.afipInvoiceService = afipInvoiceService;
        this.invoiceService = invoiceService;
        this.wsaaService = wsaaService;
        this.responseInterpreterService = afipResponseInterpreterService;
        this.restTemplate = restTemplate;
    }

    public void processQueuedInvoices() {
        List<InvoiceQueueStatus> statusesToProcess = Arrays.asList(InvoiceQueueStatus.QUEUED, InvoiceQueueStatus.RETRY);
        List<InvoiceQueue> invoicesToProcess = invoiceQueueRepository.findByStatusInOrderByEnqueuedAtAsc(statusesToProcess);
        for (InvoiceQueue invoiceQueue : invoicesToProcess) {
            // Reintento máximo 3 veces
            if (invoiceQueue.getRetryCount() >= 3) {
                invoiceQueue.setStatus(InvoiceQueueStatus.TOTAL_FAILURE);
                invoiceQueue.setProcessedAt(ZonedDateTime.now());
                invoiceQueueRepository.save(invoiceQueue);
                continue;
            }
            invoiceQueue.setStatus(InvoiceQueueStatus.BEING_PROCESSED);
            invoiceQueueRepository.save(invoiceQueue);
            processInvoice(invoiceQueue);
        }
    }

    private void processInvoice(InvoiceQueue invoiceQueue) {
        try {
            AfipAuth afipAuth = wsaaService.autenticar();
            if (afipAuth.getToken() == null && afipAuth.getErrorMessage() != null) {
                throw new RuntimeException("Fallo en la autenticacion contra AFIP: " + afipAuth.getErrorMessage());
            }
            Optional<Invoice> optInvoice = invoiceService.findById(invoiceQueue.getInvoiceId());
            if (optInvoice.isEmpty()) {
                throw new RuntimeException("Invoice not found: " + invoiceQueue.getInvoiceId());
            }
            Invoice invoice = optInvoice.get();
            Client client = invoice.getClient();
            InvoiceIvaBreakdown invoiceBreakdown = new InvoiceIvaBreakdown(invoice);
            String request = AfipSoapRequestBuilder.buildFECAESolicitarRequest(afipAuth, trisquelCUIT, invoice, client, invoiceBreakdown);
            invoiceQueue.setRequest(request);
            String responseBody = invokeFECAEWithRestTemplate(request);
            invoiceQueue.setResponse(responseBody);
            AfipResponseInterpreterService.CaeResponse interpretedResponse = responseInterpreterService.parseFecaeFromResponse(responseBody);
            handleInvoiceQueueResponseProcess(invoiceQueue, interpretedResponse, invoice);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            // TODO: handleProcessingError(invoiceQueue, e.getMessage());
        }
    }

    @Transactional
    public void handleInvoiceQueueResponseProcess(InvoiceQueue invoiceQueue,
                                                  AfipResponseInterpreterService.CaeResponse interpretedResponse,
                                                  Invoice invoice) {
        interpretedResponse.completeInvoiceQueue(invoiceQueue);
        invoiceQueue.setProcessedAt(ZonedDateTime.now());
        // Handle invoice confirmed
        if (interpretedResponse.isExitoso()) {
            invoiceQueue.setStatus(InvoiceQueueStatus.COMPLETED);
            invoiceQueueRepository.save(invoiceQueue);
            invoice.setCae(interpretedResponse.getCae());
            invoice.setVtoCae(interpretedResponse.getFechaVencimientoCae());
            invoiceService.save(invoice);
            return;
        }
        // Handle invoice that needs reprocess or failed for some reason. I think there may be cases where timeouts, etc.
        invoiceQueue.setStatus(InvoiceQueueStatus.FAILED);
        invoiceQueue.incrementRetryCount();
        invoiceQueueRepository.save(invoiceQueue);
        // Enqueue new Invoice request
        InvoiceQueue reprocessedInvoiceQueue = InvoiceQueue.createInvoiceQueueFromUncompletedInvoiceQueue(invoiceQueue);
        invoiceQueueRepository.save(reprocessedInvoiceQueue);
    }

    private String invokeFECAEWithRestTemplate(String soapRequest) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("SOAPAction", "http://ar.gov.afip.dif.FEV1/FECAESolicitar");
        headers.set("Content-Type", "text/xml; charset=utf-8");

        HttpEntity<String> requestEntity = new HttpEntity<>(soapRequest, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(fecaeUrl, HttpMethod.POST, requestEntity, String.class);
            return response.getBody();
        } catch (HttpServerErrorException e) {
            //logger.severe("Error del servidor AFIP en facturación: " + e.getStatusCode() + " - " + e.getMessage());
            throw e;
        } catch (Exception e) {
            // logger.severe("Error en llamada FECAE: " + e.getMessage());
            throw e;
        }
    }
}
