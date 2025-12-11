package trisquel.model;

import jakarta.persistence.*;
import trisquel.afip.model.ErrorType;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(name = "invoice_queue")
public class InvoiceQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_queue_seq")
    @SequenceGenerator(name = "invoice_queue_seq", sequenceName = "invoice_queue_seq", allocationSize = 1)
    private Long id;

    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "enqueued_at", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT now()")
    private ZonedDateTime enqueuedAt;

    @Column(name = "processed_at")
    private ZonedDateTime processedAt;

    @Column(name = "started_processing_at")
    private ZonedDateTime startedProcessingAt;

    @Column(name = "next_retry_at")
    private ZonedDateTime nextRetryAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 127)
    private InvoiceQueueStatus status = InvoiceQueueStatus.QUEUED;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "request")
    private String request;

    @Column(name = "response")
    private String response;

    @Column(name = "afip_status")
    private String afipStatus;

    @Column(name = "afip_cae")
    private String afipCae;

    @Column(name = "afip_due_date_cae")
    private LocalDate afipDueDateCae;

    @Column(name = "errors")
    private String errors;

    @Column(name = "observations")
    private String observations;

    @Enumerated(EnumType.STRING)
    @Column(name = "error_type", length = 50)
    private ErrorType errorType;

    @Column(name = "error_details", columnDefinition = "TEXT")
    private String errorDetails;

    @Column(name = "generated_by")
    private Long generatedBy;

    // Constructors
    public InvoiceQueue() {
    }

    public InvoiceQueue(Long invoiceId) {
        this.invoiceId = invoiceId;
        this.enqueuedAt = ZonedDateTime.now();
        this.status = InvoiceQueueStatus.QUEUED;
        this.retryCount = 0;
    }

    // Factory method para crear reintentos
    public static InvoiceQueue createRetryFromFailedQueue(InvoiceQueue failedQueue) {
        if (failedQueue.getRetryCount() >= 3) {
            return null;
        }

        InvoiceQueue retryQueue = new InvoiceQueue();
        retryQueue.setInvoiceId(failedQueue.getInvoiceId());
        retryQueue.setEnqueuedAt(ZonedDateTime.now());
        retryQueue.setStatus(InvoiceQueueStatus.QUEUED);
        retryQueue.setRetryCount(failedQueue.getRetryCount() + 1);
        retryQueue.setGeneratedBy(failedQueue.getId());

        // Calcular backoff exponencial: 1min, 5min, 15min
        retryQueue.setNextRetryAt(calculateNextRetryTime(retryQueue.getRetryCount()));

        return retryQueue;
    }

    private static ZonedDateTime calculateNextRetryTime(int retryCount) {
        ZonedDateTime now = ZonedDateTime.now();
        switch (retryCount) {
            case 1:
                return now.plusMinutes(1);
            case 2:
                return now.plusMinutes(5);
            case 3:
                return now.plusMinutes(15);
            default:
                return now.plusMinutes(1);
        }
    }

    public void markAsBeingProcessed() {
        this.status = InvoiceQueueStatus.BEING_PROCESSED;
        this.startedProcessingAt = ZonedDateTime.now();
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public ZonedDateTime getEnqueuedAt() {
        return enqueuedAt;
    }

    public void setEnqueuedAt(ZonedDateTime enqueuedAt) {
        this.enqueuedAt = enqueuedAt;
    }

    public ZonedDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(ZonedDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public ZonedDateTime getStartedProcessingAt() {
        return startedProcessingAt;
    }

    public void setStartedProcessingAt(ZonedDateTime startedProcessingAt) {
        this.startedProcessingAt = startedProcessingAt;
    }

    public ZonedDateTime getNextRetryAt() {
        return nextRetryAt;
    }

    public void setNextRetryAt(ZonedDateTime nextRetryAt) {
        this.nextRetryAt = nextRetryAt;
    }

    public InvoiceQueueStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceQueueStatus status) {
        this.status = status;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getAfipStatus() {
        return afipStatus;
    }

    public void setAfipStatus(String afipStatus) {
        this.afipStatus = afipStatus;
    }

    public String getAfipCae() {
        return afipCae;
    }

    public void setAfipCae(String afipCae) {
        this.afipCae = afipCae;
    }

    public LocalDate getAfipDueDateCae() {
        return afipDueDateCae;
    }

    public void setAfipDueDateCae(LocalDate afipDueDateCae) {
        this.afipDueDateCae = afipDueDateCae;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    public Long getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(Long generatedBy) {
        this.generatedBy = generatedBy;
    }
}