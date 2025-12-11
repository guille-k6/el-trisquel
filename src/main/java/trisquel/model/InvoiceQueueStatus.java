package trisquel.model;

public enum InvoiceQueueStatus {
    QUEUED,           // To be processed (includes retries)
    BEING_PROCESSED,  // Being currently processed
    FAILED,           // Failed, but a new InvoiceQueue with QUEUED status will be generated
    TOTAL_FAILURE,    // Out of retries or non-recoverable error
    COMPLETED         // Successfully processed
}
