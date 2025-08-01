package trisquel.model;

public enum InvoiceQueueStatus {
    QUEUED, // To be processed
    BEING_PROCESSED, // Being currently processed
    RETRY, // Retrying (afip says in some cases, comes from a failed request)
    FAILED, // Failed, but a new InvoiceQueue with Queued status will be generated
    TOTAL_FAILURE, // Out of retries
    COMPLETED,
}
