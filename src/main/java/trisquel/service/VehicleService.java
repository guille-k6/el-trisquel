package trisquel.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trisquel.model.ConfigurationMap;
import trisquel.model.DailyBook;
import trisquel.model.Vehicle;
import trisquel.repository.DailyBookRepository;
import trisquel.repository.VehicleRepository;
import trisquel.utils.ValidationErrorItem;
import trisquel.utils.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VehicleService {
    @Autowired
    VehicleService(VehicleRepository repository, DailyBookRepository dailyBookRepository,
                   ConfigurationService configurationService) {
        this.repository = repository;
        this.dailyBookRepository = dailyBookRepository;
        this.configurationService = configurationService;
    }

    private final VehicleRepository repository;
    private final DailyBookRepository dailyBookRepository;
    private final ConfigurationService configurationService;

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
        ValidationException.verifyAndMaybeThrowValidationException(validationErrors);
    }


    public void delete(Long id) {
        List<DailyBook> dailyBooksReferred = dailyBookRepository.findByVehicle(id);
        if (!dailyBooksReferred.isEmpty()) {
            Set<Long> idsReferred = dailyBooksReferred.stream().map(DailyBook::getId).collect(Collectors.toSet());
            ValidationException validationException = new ValidationException();
            validationException.addValidationError("Error", "El vehiculo está siendo referenciado en los libros diarios: " + idsReferred);
            throw validationException;
        }
        repository.deleteById(id);
    }

    public Optional<Vehicle> getDefaultVehicle() {
        Optional<ConfigurationMap> defaultEntities = configurationService.findByKey("default");
        if (defaultEntities.isEmpty()) {
            return Optional.empty();
        }
        JsonNode defaultVehicle = defaultEntities.get().getValue().get("vehiculoDefault");
        if (defaultVehicle == null) {
            return Optional.empty();
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setId(defaultVehicle.get("id").asLong());
        vehicle.setName(defaultVehicle.get("name").asText());
        return Optional.of(vehicle);
    }
}
