package com.trisquel.service;

import com.trisquel.model.Invoice;
import com.trisquel.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public
class InvoiceService {
    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.repository = invoiceRepository;
    }
    private final InvoiceRepository repository;
    public List<Invoice> findAll() { return repository.findAll(); }
    public Optional<Invoice> findById(Long id) { return repository.findById(id); }
    public Invoice save(Invoice invoice) { return repository.save(invoice); }
    public void delete(Long id) { repository.deleteById(id); }
}
