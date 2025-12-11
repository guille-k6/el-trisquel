package trisquel.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import trisquel.model.InvoiceQueue;
import trisquel.model.InvoiceQueueStatus;
import trisquel.repository.InvoiceQueueRepository;
import trisquel.utils.ValidationException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceQueueService {
    InvoiceQueueRepository repository;

    public InvoiceQueueService(InvoiceQueueRepository invoiceQueueRepository) {
        this.repository = invoiceQueueRepository;
    }

    public Page<InvoiceQueue> findAll(int page, LocalDate dateFrom, LocalDate dateTo, InvoiceQueueStatus status) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("enqueuedAt").descending());
        Specification<InvoiceQueue> spec = Specification.where(null);
        if (dateFrom != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("enqueuedAt"), dateFrom));
        }
        if (dateTo != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("enqueuedAt"), dateTo));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        Page<InvoiceQueue> invoiceQueuesPage = repository.findAll(spec, pageable);
        return invoiceQueuesPage;
    }

    public Optional<InvoiceQueue> findById(Long id) {
        return repository.findById(id);
    }

    public List<InvoiceQueue> findByInvoice(Long invoiceId) {
        return repository.findByInvoiceId(invoiceId);
    }

    public void retryInvoiceQueue(Long id) {
        Optional<InvoiceQueue> invoiceQueue = repository.findById(id);
        if (invoiceQueue.isEmpty()) throw new ValidationException().addValidationError("Error", "No existe cola de facturación con id: " + id);
        List<InvoiceQueueStatus> retryableStates = Arrays.asList(InvoiceQueueStatus.QUEUED, InvoiceQueueStatus.FAILED);
        if (!retryableStates.contains(invoiceQueue.get().getStatus())) {
            throw new ValidationException().addValidationError("Error", "El estado de la cola de facturación no es el correcto");
        }

    }
}
