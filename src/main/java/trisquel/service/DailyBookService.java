package trisquel.service;

import trisquel.model.DailyBook;
import trisquel.model.DailyBookItem;
import trisquel.model.Dto.DailyBookDTO;
import trisquel.repository.DailyBookRepository;
import trisquel.utils.ValidationErrorItem;
import trisquel.utils.ValidationException;
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
        processDailyBookItems(dailyBook);
        return repository.save(dailyBook);
    }

    private void processDailyBookItems(DailyBook dailyBook) {
        for (DailyBookItem item : dailyBook.getItems()) {
            // Helper to mantain bidirectional navigation
            item.setDailyBook(dailyBook);
            // Validations
            validateDailyBookItem(item);
            // Save sequencer state
        }
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
        if (dailyBook.getVehicle() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El libro diario debe tener vehiculo"));
        }
        if (dailyBook.getDate() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El libro diario debe tener fecha"));
        }
        if (dailyBook.getVehicleKmsBefore() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El vehiculo debe tener kilómetros iniciales"));
        }
        if (dailyBook.getVehicleKmsAfter() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El vehiculo debe tener kilómetros finales"));
        }
        if (dailyBook.getVehicleKmsBefore() >= dailyBook.getVehicleKmsAfter()) {
            validationErrors.add(new ValidationErrorItem("Error", "Los kilómetros finales no pueden ser menores a los iniciales"));
        }
        if (dailyBook.getVehicle() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El libro diario debe tener vehiculo"));
        }
        if (dailyBook.getNitrogenProvider() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El libro diario debe tener proveedor de nitrógeno"));
        }
        if (dailyBook.getPressureTankBefore() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El tanque debe tener presión inicial"));
        }
        if (dailyBook.getPressureTankAfter() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El tanque debe tener presión final"));
        }
        if (dailyBook.getKgTankBefore() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El tanque debe tener peso inicial"));
        }
        if (dailyBook.getKgTankAfter() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El tanque debe tener peso final"));
        }
        if (dailyBook.getKgTankAfter() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El tanque debe tener peso final"));
        }
        if (dailyBook.getLtExtractedTank() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "Los litros totales extraídos no deben ser nulos"));
        }
        if (dailyBook.getLtTotalFlask() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "Los litros totales en termos no deben ser nulos"));
        }
        if (dailyBook.getLtRemainingFlask() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "Los litros remanentes en termos no deben ser nulos"));
        }
        ValidationException.verifyAndMaybeThrowValidationException(validationErrors);
    }

    private void validateDailyBookItem(DailyBookItem item) {
        List<ValidationErrorItem> validationErrors = new ArrayList<>();
        if (item.getClient() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "La descarga " + item.getId() + " debe tener cliente"));
        }
        // Para el caso de cliente anulado no validamos nada
        if (10L == item.getClient().getId()) {
            return;
        }
        if (item.getProduct() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "La descarga " + item.getId() + " debe tener producto"));
        }
        if (item.getAmount() == null || item.getAmount() <= 0) {
            validationErrors.add(new ValidationErrorItem("Error", "La descarga " + item.getId() + " debe tener cantidad mayor a 0"));
        }
        if (item.getDate() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "La descarga " + item.getId() + " debe tener fecha"));
        }
        ValidationException.verifyAndMaybeThrowValidationException(validationErrors);
    }
}
