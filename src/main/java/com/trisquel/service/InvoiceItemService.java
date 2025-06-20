package com.trisquel.service;

import com.trisquel.model.InvoiceItem;
import com.trisquel.repository.InvoiceItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceItemService {

    @Autowired
    public InvoiceItemService(InvoiceItemRepository invoiceItemRepository) {
        this.repository = invoiceItemRepository;
    }

    private final InvoiceItemRepository repository;

    public List<InvoiceItem> findAll() {
        return repository.findAll();
    }

    public Optional<InvoiceItem> findById(Long id) {
        return repository.findById(id);
    }

    public InvoiceItem save(InvoiceItem invoice) {
        return repository.save(invoice);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
