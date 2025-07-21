package trisquel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trisquel.model.InvoiceQueueRecord;

import java.util.List;

@Repository
public interface InvoiceQueueRecordRepository extends JpaRepository<InvoiceQueueRecord, Long> {
    List<InvoiceQueueRecord> findByInvoiceId(Long invoiceId);
}
