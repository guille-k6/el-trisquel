package trisquel.service;

import org.springframework.stereotype.Service;
import trisquel.model.InvoiceQueueRecord;
import trisquel.repository.InvoiceQueueRecordRepository;

import java.util.List;

@Service
public class InvoiceQueueRecordService {

    InvoiceQueueRecordRepository repository;

    InvoiceQueueRecordService(InvoiceQueueRecordRepository invoiceQueueRecordRepository) {
        this.repository = invoiceQueueRecordRepository;
    }

    public void save(InvoiceQueueRecord invoiceQueue) {
        repository.save(invoiceQueue);
    }

    public List<InvoiceQueueRecord> findByInvoiceId(Long invoiceId) {
        return repository.findByInvoiceId(invoiceId);
    }
}
