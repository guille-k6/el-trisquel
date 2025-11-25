package trisquel.async;

public class InvoiceCreatedEvent {
    private final Long invoiceId;

    public InvoiceCreatedEvent(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }
}
