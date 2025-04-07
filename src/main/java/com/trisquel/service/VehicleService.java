package com.trisquel.service;

import com.trisquel.model.Vehicle;
import com.trisquel.repository.VehicleRepository;
import com.trisquel.utils.ValidationErrorItem;
import com.trisquel.utils.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {
    @Autowired
    VehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    private final VehicleRepository repository;

    public List<Vehicle> findAll() {
        return repository.findAll();
    }

    public Optional<Vehicle> findById(Long id) {
        return repository.findById(id);
    }

    public Vehicle save(Vehicle vehicle) {
        validateVehicle(vehicle);
        return repository.save(vehicle);
    }

    private void validateVehicle(Vehicle vehicle) {
        List<ValidationErrorItem> validationErrors = new ArrayList<>();
        if (vehicle.getId() == null || vehicle.getId() == 0) {
            vehicle.setId(null);
        } else {
            // This is an update, verify the entity exists
            Optional<Vehicle> existingVehicle = repository.findById(vehicle.getId());
            if (existingVehicle.isEmpty()) {
                ValidationException validationException = new ValidationException();
                validationException.addValidationError("Error", "Vehiculo no encontrado");
                throw validationException;
            }
        }
        if (vehicle.getName().isBlank()) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo nombre es obligatorio"));
        }
        if (vehicle.getPurchaseDate() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo fecha de compra es obligatorio"));
        }
        if (vehicle.getPurchaseDatePrice() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo precio de compra es obligatorio"));
        }
        if (vehicle.getPurchaseDatePrice() < 0) {
            validationErrors.add(new ValidationErrorItem("Error", "El precio de compra no puede ser menor a 0"));
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
