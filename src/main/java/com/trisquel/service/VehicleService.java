package com.trisquel.service;

import com.trisquel.model.Vehicle;
import com.trisquel.repository.VehicleRepository;
import com.trisquel.utils.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        // TODO: Validaciones que debe tener un vehiculo, ej: nombre no nulo, precio de compra > 0
        ValidationException validationException = new ValidationException();
        validationException.addValidationError("EJEMPLO", "El nombre es invalido");
        throw validationException;
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
