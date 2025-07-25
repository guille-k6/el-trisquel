package trisquel.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(name = "invoice_queue")
public class  InvoiceQueue {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 127)
    private InvoiceQueueStatus status = InvoiceQueueStatus.QUEUED;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "request")
    private String request;

    @Column(name = "response")
    private String response;

    // Constructors
    public InvoiceQueue() {
    }

    public InvoiceQueue(Long invoiceId) {
        this.invoiceId = invoiceId;
        this.enqueuedAt = ZonedDateTime.now();
        this.status = InvoiceQueueStatus.QUEUED;
        this.retryCount = 0;
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

    // Utility methods
    public void markAsProcessed() {
        this.processedAt = ZonedDateTime.now();
        this.status = InvoiceQueueStatus.COMPLETED;
    }

    public void markAsFailed(String errorMessage) {
        this.processedAt = ZonedDateTime.now();
        this.status = InvoiceQueueStatus.FAILING;
    }

    public void incrementRetryCount() {
        this.retryCount = (this.retryCount == null ? 0 : this.retryCount) + 1;
    }
}
