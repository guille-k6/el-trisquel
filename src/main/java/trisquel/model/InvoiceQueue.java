package trisquel.model;

import jakarta.persistence.*;

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
    @Column(name = "afip_reprocess")
    private String afipReprocess;
    @Column(name = "afip_cae")
    private String afipCae;
    @Column(name = "afip_due_date_cae")
    private LocalDate afipDueDateCae;
    @Column(name = "errors")
    private String errors;
    @Column(name = "observations")
    private String observations;
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

    public String getAfipStatus() {
        return afipStatus;
    }

    public void setAfipStatus(String afipStatus) {
        this.afipStatus = afipStatus;
    }

    public String getAfipReprocess() {
        return afipReprocess;
    }

    public void setAfipReprocess(String afipReprocess) {
        this.afipReprocess = afipReprocess;
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

    public Long getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(Long generatedBy) {
        this.generatedBy = generatedBy;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public void incrementRetryCount() {
        this.retryCount = (this.retryCount == null ? 1 : this.retryCount) + 1;
    }

    public static InvoiceQueue createInvoiceQueueFromUncompletedInvoiceQueue(InvoiceQueue invoiceQueue) {
        InvoiceQueue newIQueue = new InvoiceQueue();
        newIQueue.setInvoiceId(invoiceQueue.getInvoiceId());
        newIQueue.setEnqueuedAt(ZonedDateTime.now());
        if (!"N".equals(invoiceQueue.getAfipReprocess())) {
            invoiceQueue.setStatus(InvoiceQueueStatus.RETRY);
        } else {
            invoiceQueue.setStatus(InvoiceQueueStatus.FAILED);
        }
        newIQueue.setRetryCount(invoiceQueue.getRetryCount());
        newIQueue.setGeneratedBy(invoiceQueue.getId());
        return newIQueue;
    }
}
