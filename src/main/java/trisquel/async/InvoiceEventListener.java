package trisquel.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import trisquel.afip.service.InvoiceProcessingService;

@Component
public class InvoiceEventListener {

    private final InvoiceProcessingService invoiceProcessingService;

    public InvoiceEventListener(InvoiceProcessingService invoiceProcessingService) {
        this.invoiceProcessingService = invoiceProcessingService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleInvoiceCreated(InvoiceCreatedEvent event) {
        invoiceProcessingService.processQueuedInvoices();
    }
}
