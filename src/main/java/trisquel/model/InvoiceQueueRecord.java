package trisquel.model;

import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "invoice_queue_record")
public class InvoiceQueueRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_queue_record_seq")
    @SequenceGenerator(name = "invoice_queue_record_seq", sequenceName = "invoice_queue_record_seq", allocationSize = 1)
    private Long id;

    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "invoice_queue_id")
    private Long invoiceQueueId;

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

    public static InvoiceQueueRecord buildFromInvoiceQueue(InvoiceQueue invoiceQueue) {
        InvoiceQueueRecord record = new InvoiceQueueRecord();
        record.setId(null);
        record.setInvoiceId(invoiceQueue.getInvoiceId());
        record.setInvoiceQueueId(invoiceQueue.getId());
        record.setEnqueuedAt(invoiceQueue.getEnqueuedAt());
        ZonedDateTime processTime = invoiceQueue.getProcessedAt();
        if (processTime != null) {
            record.setProcessedAt(processTime);
        } else {
            record.setProcessedAt(ZonedDateTime.now());
        }
        record.setProcessedAt(invoiceQueue.getProcessedAt());
        record.setStatus(invoiceQueue.getStatus());
        record.setRetryCount(invoiceQueue.getRetryCount());
        record.setRequest(invoiceQueue.getRequest());
        record.setResponse(invoiceQueue.getResponse());
        return record;
    }

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

    public Long getInvoiceQueueId() {
        return invoiceQueueId;
    }

    public void setInvoiceQueueId(Long invoiceQueueId) {
        this.invoiceQueueId = invoiceQueueId;
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
}
