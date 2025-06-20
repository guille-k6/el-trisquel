package com.trisquel.repository;

import com.trisquel.model.InvoiceQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceQueueRepository extends JpaRepository<InvoiceQueue, Long> {

    Optional<InvoiceQueue> findByInvoiceId(Long invoiceId);

    List<InvoiceQueue> findByStatus(String status);

    List<InvoiceQueue> findByStatusOrderByEnqueuedAtAsc(String status);

    @Query("SELECT iq FROM InvoiceQueue iq WHERE iq.status = 'queued' ORDER BY iq.enqueuedAt ASC")
    List<InvoiceQueue> findQueuedItems();

    // Find failed items that can be retried
    @Query("SELECT iq FROM InvoiceQueue iq WHERE iq.status = 'failed' AND iq.retryCount < :maxRetries ORDER BY iq.enqueuedAt ASC")
    List<InvoiceQueue> findRetryableFailedItems(@Param("maxRetries") int maxRetries);

    List<InvoiceQueue> findByEnqueuedAtBefore(ZonedDateTime dateTime);

    List<InvoiceQueue> findByProcessedAtAfter(ZonedDateTime dateTime);

    Optional<InvoiceQueue> findByCae(String cae);

    Optional<InvoiceQueue> findByArcaRequestId(String arcaRequestId);

    long countByStatus(String status);

    List<InvoiceQueue> findByRetryCountGreaterThan(int retryCount);

    @Query("SELECT iq FROM InvoiceQueue iq WHERE iq.status = 'queued' AND iq.enqueuedAt < :cutoffTime")
    List<InvoiceQueue> findStaleQueuedItems(@Param("cutoffTime") ZonedDateTime cutoffTime);

    @Query("SELECT iq FROM InvoiceQueue iq WHERE iq.status IN :statuses ORDER BY iq.enqueuedAt ASC")
    List<InvoiceQueue> findByStatusIn(@Param("statuses") List<String> statuses);

    boolean existsByInvoiceId(Long invoiceId);

    // Delete processed items older than specified date
    void deleteByStatusAndProcessedAtBefore(String status, ZonedDateTime cutoffDate);
}
