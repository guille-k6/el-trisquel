package trisquel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import trisquel.model.InvoiceQueue;
import trisquel.model.InvoiceQueueStatus;

import java.util.List;

@Repository
public interface InvoiceQueueRepository extends JpaRepository<InvoiceQueue, Long>, JpaSpecificationExecutor<InvoiceQueue> {

    List<InvoiceQueue> findByStatus(String status);

    List<InvoiceQueue> findByStatusOrderByEnqueuedAtAsc(String status);

    long countByStatus(InvoiceQueueStatus status);

    @Query("SELECT iq FROM InvoiceQueue iq WHERE iq.status IN :statuses ORDER BY iq.enqueuedAt ASC")
    List<InvoiceQueue> findByStatusInOrderByEnqueuedAtAsc(@Param("statuses") List<InvoiceQueueStatus> statuses);

    @Query("SELECT iq FROM InvoiceQueue iq WHERE iq.invoiceId = :invoiceId ORDER BY iq.enqueuedAt DESC")
    List<InvoiceQueue> findByInvoiceId(@Param("invoiceId") Long invoiceId);
}
