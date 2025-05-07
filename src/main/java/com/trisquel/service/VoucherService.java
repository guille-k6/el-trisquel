package com.trisquel.service;

import com.trisquel.model.Voucher;
import com.trisquel.repository.VoucherRepository;
import com.trisquel.utils.ValidationErrorItem;
import com.trisquel.utils.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VoucherService {
    private final VoucherRepository repository;

    @Autowired
    public VoucherService(VoucherRepository repository) {
        this.repository = repository;
    }

    public List<Voucher> findAll() {
        return repository.findAll();
    }

    public Optional<Voucher> findById(Long id) {
        return repository.findById(id);
    }

    public Voucher save(Voucher voucher) {
        validateVoucher(voucher);
        return repository.save(voucher);
    }

    private void validateVoucher(Voucher voucher) {
        List<ValidationErrorItem> validationErrors = new ArrayList<>();
        if (voucher.getId() == null || voucher.getId() == 0) {
            voucher.setId(null);
        } else {
            // This is an update, verify the entity exists
            Optional<Voucher> existingVoucher = repository.findById(voucher.getId());
            if (existingVoucher.isEmpty()) {
                ValidationException validationException = new ValidationException();
                validationException.addValidationError("Error", "Voucher no encontrado");
                throw validationException;
            }
        }
        if (voucher.getName() == null || voucher.getName().isBlank()) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo nombre es obligatorio"));
        }
        if (!validationErrors.isEmpty()) {
            ValidationException validationException = new ValidationException();
            for (ValidationErrorItem validationErrorItem : validationErrors) {
                validationException.addValidationError(validationErrorItem.title(), validationErrorItem.message());
            }
            throw validationException;
        }
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}


