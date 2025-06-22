package com.trisquel.service;

import com.trisquel.model.InvoiceQueue;
import com.trisquel.model.InvoiceQueueStatus;
import com.trisquel.repository.InvoiceQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InvoiceQueueService {

    @Autowired
    private InvoiceQueueRepository invoiceQueueRepository;

    // Basic CRUD operations
    public InvoiceQueue save(InvoiceQueue invoiceQueue) {
        return invoiceQueueRepository.save(invoiceQueue);
    }

    public Optional<InvoiceQueue> findById(Long id) {
        return invoiceQueueRepository.findById(id);
    }

    public List<InvoiceQueue> findAll() {
        return invoiceQueueRepository.findAll();
    }

    public void deleteById(Long id) {
        invoiceQueueRepository.deleteById(id);
    }

    // Business logic methods

    /**
     * Enqueue an invoice for processing
     */
    public InvoiceQueue enqueueInvoice(Long invoiceId) {
        // Check if invoice is already in queue
        if (invoiceQueueRepository.existsByInvoiceId(invoiceId)) {
            throw new IllegalArgumentException("Invoice " + invoiceId + " is already in queue");
        }

        InvoiceQueue queueItem = new InvoiceQueue(invoiceId);
        return invoiceQueueRepository.save(queueItem);
    }

    /**
     * Get next queued item for processing
     */
    public Optional<InvoiceQueue> getNextQueuedItem() {
        List<InvoiceQueue> queuedItems = invoiceQueueRepository.findQueuedItems();
        return queuedItems.isEmpty() ? Optional.empty() : Optional.of(queuedItems.get(0));
    }

    /**
     * Get all queued items
     */
    public List<InvoiceQueue> getQueuedItems() {
        return invoiceQueueRepository.findByStatus("queued");
    }

    /**
     * Mark item as processed successfully
     */
    public InvoiceQueue markAsProcessed(Long id, String cae, String invoiceNumber) {
        InvoiceQueue item = invoiceQueueRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Queue item not found: " + id));

        item.markAsProcessed();
        item.setCae(cae);
        item.setInvoiceNumber(invoiceNumber);

        return invoiceQueueRepository.save(item);
    }

    /**
     * Mark item as failed
     */
    public InvoiceQueue markAsFailed(Long id, String errorMessage) {
        InvoiceQueue item = invoiceQueueRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Queue item not found: " + id));

        item.markAsFailed(errorMessage);
        item.incrementRetryCount();

        return invoiceQueueRepository.save(item);
    }

    /**
     * Retry a failed item
     */
    public InvoiceQueue retryFailedItem(Long id) {
        InvoiceQueue item = invoiceQueueRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Queue item not found: " + id));

        if (!InvoiceQueueStatus.FAILING.equals(item.getStatus())) {
            throw new IllegalStateException("Item is not in failed status");
        }

        item.setStatus(InvoiceQueueStatus.RETRYING);
        item.setProcessedAt(null);
        item.setErrorMessage(null);
        item.incrementRetryCount();

        return invoiceQueueRepository.save(item);
    }

    /**
     * Get failed items that can be retried
     */
    public List<InvoiceQueue> getRetryableFailedItems(int maxRetries) {
        return invoiceQueueRepository.findRetryableFailedItems(maxRetries);
    }

    /**
     * Update ARCA request ID
     */
    public InvoiceQueue updateArcaRequestId(Long id, String arcaRequestId) {
        InvoiceQueue item = invoiceQueueRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Queue item not found: " + id));

        item.setArcaRequestId(arcaRequestId);
        return invoiceQueueRepository.save(item);
    }

    /**
     * Find by invoice ID
     */
    public Optional<InvoiceQueue> findByInvoiceId(Long invoiceId) {
        return invoiceQueueRepository.findByInvoiceId(invoiceId);
    }

    /**
     * Find by CAE
     */
    public Optional<InvoiceQueue> findByCae(String cae) {
        return invoiceQueueRepository.findByCae(cae);
    }

    /**
     * Find by ARCA request ID
     */
    public Optional<InvoiceQueue> findByArcaRequestId(String arcaRequestId) {
        return invoiceQueueRepository.findByArcaRequestId(arcaRequestId);
    }

    /**
     * Get queue statistics
     */
    public QueueStatistics getQueueStatistics() {
        long queuedCount = invoiceQueueRepository.countByStatus("queued");
        long processedCount = invoiceQueueRepository.countByStatus("processed");
        long failedCount = invoiceQueueRepository.countByStatus("failed");

        return new QueueStatistics(queuedCount, processedCount, failedCount);
    }

    /**
     * Find stale queued items (older than specified hours)
     */
    public List<InvoiceQueue> findStaleQueuedItems(int hours) {
        ZonedDateTime cutoffTime = ZonedDateTime.now().minusHours(hours);
        return invoiceQueueRepository.findStaleQueuedItems(cutoffTime);
    }

    /**
     * Clean up old processed items
     */
    public void cleanupOldProcessedItems(int daysOld) {
        ZonedDateTime cutoffDate = ZonedDateTime.now().minusDays(daysOld);
        invoiceQueueRepository.deleteByStatusAndProcessedAtBefore("processed", cutoffDate);
    }

    /**
     * Requeue stale items
     */
    public int requeueStaleItems(int hoursOld) {
        List<InvoiceQueue> staleItems = findStaleQueuedItems(hoursOld);
        int count = 0;

        for (InvoiceQueue item : staleItems) {
            item.setEnqueuedAt(ZonedDateTime.now());
            item.incrementRetryCount();
            invoiceQueueRepository.save(item);
            count++;
        }

        return count;
    }

    // Inner class for statistics
    public static class QueueStatistics {
        private final long queuedCount;
        private final long processedCount;
        private final long failedCount;

        public QueueStatistics(long queuedCount, long processedCount, long failedCount) {
            this.queuedCount = queuedCount;
            this.processedCount = processedCount;
            this.failedCount = failedCount;
        }

        public long getQueuedCount() {
            return queuedCount;
        }

        public long getProcessedCount() {
            return processedCount;
        }

        public long getFailedCount() {
            return failedCount;
        }

        public long getTotalCount() {
            return queuedCount + processedCount + failedCount;
        }

        @Override
        public String toString() {
            return String.format("QueueStatistics{queued=%d, processed=%d, failed=%d, total=%d}", queuedCount, processedCount, failedCount, getTotalCount());
        }
    }
}
