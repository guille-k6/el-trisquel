package com.trisquel.service;

import com.trisquel.model.DailyBook;
import com.trisquel.model.DailyBookItem;
import com.trisquel.model.Dto.DailyBookDTO;
import com.trisquel.repository.DailyBookRepository;
import com.trisquel.utils.ValidationErrorItem;
import com.trisquel.utils.ValidationException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DailyBookService {
    @Autowired
    public DailyBookService(DailyBookRepository repository) {
        this.repository = repository;
    }

    private final DailyBookRepository repository;

    public List<DailyBookDTO> findAll() {
        List<DailyBook> dailyBooks = repository.findAll();
        return DailyBookDTO.translateToDTOs(dailyBooks);
    }

    public Optional<DailyBookDTO> findById(Long id) {
        Optional<DailyBook> dailyBook = repository.findById(id);
        if (dailyBook.isEmpty()) {
            return Optional.empty();
        }
        DailyBookDTO dailyBookDTO = DailyBookDTO.translateToDTO(dailyBook.get());
        return Optional.of(dailyBookDTO);
    }

    @Transactional
    public DailyBook save(DailyBook dailyBook) {
        validateDailyBook(dailyBook);
        // Helper to mantain bidirectional navigation
        for (DailyBookItem item : dailyBook.getItems()) {
            item.setDailyBook(dailyBook);
        }
        return repository.save(dailyBook);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private void validateDailyBook(DailyBook dailyBook) {
        List<ValidationErrorItem> validationErrors = new ArrayList<>();
        if (dailyBook.getId() == null || dailyBook.getId() == 0) {
            dailyBook.setId(null);
        } else {
            // This is an update, verify the entity exists
            Optional<DailyBook> existingDailyBook = repository.findById(dailyBook.getId());
            if (existingDailyBook.isEmpty()) {
                ValidationException validationException = new ValidationException();
                validationException.addValidationError("Error", "Libro diario no encontrado");
                throw validationException;
            }
        }
        if (dailyBook.getItems().isEmpty()) {
            validationErrors.add(new ValidationErrorItem("Error", "El libro diario debe tener al menos un item"));
        }
        // TODO: Validaciones en profundidad
        if (!validationErrors.isEmpty()) {
            ValidationException validationException = new ValidationException();
            for (ValidationErrorItem validationErrorItem : validationErrors) {
                validationException.addValidationError(validationErrorItem.title(), validationErrorItem.message());
            }
            throw validationException;
        }
    }
}
