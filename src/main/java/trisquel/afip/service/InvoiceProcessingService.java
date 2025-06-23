package trisquel.afip.service;

import trisquel.afip.config.AfipConfiguration;
import trisquel.afip.model.AfipInvoiceRequest;
import trisquel.afip.model.AfipInvoiceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trisquel.model.InvoiceQueue;
import trisquel.model.InvoiceQueueStatus;
import trisquel.repository.InvoiceQueueRepository;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class InvoiceProcessingService {

    private final InvoiceQueueRepository invoiceQueueRepository;
    private final AfipInvoiceService afipInvoiceService;
    private final AfipConfiguration afipConfig;

    public InvoiceProcessingService(InvoiceQueueRepository invoiceQueueRepository,
                                    AfipInvoiceService afipInvoiceService, AfipConfiguration afipConfig) {
        this.invoiceQueueRepository = invoiceQueueRepository;
        this.afipInvoiceService = afipInvoiceService;
        this.afipConfig = afipConfig;
    }

    public void processQueuedInvoices() {
        // log.info("Iniciando procesamiento de facturas en cola...");
        List<InvoiceQueueStatus> statusesToProcess = Arrays.asList(InvoiceQueueStatus.QUEUED, InvoiceQueueStatus.FAILING);
        List<InvoiceQueue> invoicesToProcess = invoiceQueueRepository.findByStatusInOrderByEnqueuedAtAsc(statusesToProcess);
        // log.info("Encontradas {} facturas para procesar", invoicesToProcess.size());
        for (InvoiceQueue invoiceQueue : invoicesToProcess) {
            try {
                processInvoice(invoiceQueue);
            } catch (Exception e) {
                // log.error("Error procesando factura ID: {}", invoiceQueue.getId(), e);
                handleProcessingError(invoiceQueue, e.getMessage());
            }
        }
    }

    private void processInvoice(InvoiceQueue invoiceQueue) {
        // Actualizar estado
        updateInvoiceStatus(invoiceQueue);
        // Construir request para AFIP
        AfipInvoiceRequest afipRequest = buildAfipRequest(invoiceQueue);
        // Enviar a AFIP
        AfipInvoiceResponse afipResponse = afipInvoiceService.authorizeInvoice(afipRequest);
        // Procesar respuesta
        handleAfipResponse(invoiceQueue, afipResponse);
    }

    private AfipInvoiceRequest buildAfipRequest(InvoiceQueue invoiceQueue) {
        AfipInvoiceRequest afipRequest = new AfipInvoiceRequest();
        // TODO: Ver absolutamente todo de com ocrear una request de nitrogeno liquido
        return afipRequest;
    }

    private void handleAfipResponse(InvoiceQueue invoiceQueue, AfipInvoiceResponse response) {
        if ("A".equals(response.getResult())) {
            // Aprobado
            invoiceQueue.setStatus(InvoiceQueueStatus.COMPLETED);
            invoiceQueue.setCae(response.getCae());
            invoiceQueue.setCaeDueDate(response.getCaeDueDate());
            invoiceQueue.setErrorMessage(null);
            // log.info("Factura autorizada. ID: {} - CAE: {}", invoiceQueue.getId(), response.getCae());
        } else {
            // TODO: Mejorar Rechazado
            String errors = String.join(", ", response.getErrors());
            handleProcessingError(invoiceQueue, "AFIP rechazó la factura: " + errors);
        }
        invoiceQueueRepository.save(invoiceQueue);
    }

    private void updateInvoiceStatus(InvoiceQueue invoiceQueue) {
        if (invoiceQueue.getStatus() == InvoiceQueueStatus.QUEUED) {
            invoiceQueue.setStatus(InvoiceQueueStatus.STARTED);
        } else if (invoiceQueue.getStatus() == InvoiceQueueStatus.FAILING) {
            invoiceQueue.setStatus(InvoiceQueueStatus.RETRYING);
            invoiceQueue.setRetryCount(invoiceQueue.getRetryCount() + 1);
        }
        invoiceQueue.setProcessedAt(ZonedDateTime.now());
        invoiceQueueRepository.save(invoiceQueue);
        //log.info("Estado actualizado para factura ID: {} - Nuevo estado: {}", invoiceQueue.getId(), invoiceQueue.getStatus());
    }

    private void handleProcessingError(InvoiceQueue invoiceQueue, String errorMessage) {
        invoiceQueue.setErrorMessage(errorMessage);
        if (invoiceQueue.getRetryCount() >= afipConfig.getMaxRetries()) {
            invoiceQueue.setStatus(InvoiceQueueStatus.FAILURE);
            // log.error("Factura marcada como FAILED después de {} intentos. ID: {}", afipConfig.getMaxRetries(), invoiceQueue.getId());
        } else {
            invoiceQueue.setStatus(InvoiceQueueStatus.FAILING);
            // log.warn("Factura marcada como FAILING. ID: {} - Intento: {}/{}", invoiceQueue.getId(), invoiceQueue.getRetryCount(), afipConfig.getMaxRetries());
        }
        invoiceQueueRepository.save(invoiceQueue);
    }
}
