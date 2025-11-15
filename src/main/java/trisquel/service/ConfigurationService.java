package trisquel.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import trisquel.model.ConfigurationMap;
import trisquel.model.NitrogenProvider;
import trisquel.repository.ConfigurationRepository;
import trisquel.utils.ValidationErrorItem;
import trisquel.utils.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConfigurationService {

    ConfigurationRepository repository;

    ConfigurationService(ConfigurationRepository repository) {
        this.repository = repository;
    }

    public Optional<ConfigurationMap> findByKey(String key) {
        return repository.findByKey(key);
    }

    public void save(ConfigurationMap configurationMap) {
        validateConfiguration(configurationMap);
        repository.save(configurationMap);
    }

    private void validateConfiguration(ConfigurationMap configurationMap) {
        List<ValidationErrorItem> validationErrors = new ArrayList<>();
        if (configurationMap.getId() == null || configurationMap.getId() == 0) {
            configurationMap.setId(null);
        } else {
            // This is an update, verify the entity exists
            Optional<ConfigurationMap> configMap = repository.findById(configurationMap.getId());
            if (configMap.isEmpty()) {
                ValidationException validationException = new ValidationException();
                validationException.addValidationError("Error", "Configuración no encontrada");
                throw validationException;
            }
        }
        if (configurationMap.getKey().isBlank()) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo clave es obligatorio"));
        }
        if (configurationMap.getValue() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo valor es obligatorio"));
        }
        ValidationException.verifyAndMaybeThrowValidationException(validationErrors);
    }

    public Optional<NitrogenProvider> getDefaultNitrogenProvider() {
        Optional<ConfigurationMap> defaultEntities = repository.findByKey("default");
        if (defaultEntities.isEmpty()) {
            return Optional.empty();
        }
        JsonNode defaultNitrogenProvider = defaultEntities.get().getValue().get("proveedorDefault");
        if (defaultNitrogenProvider == null) {
            return Optional.empty();
        }
        NitrogenProvider proveedor = NitrogenProvider.fromId(defaultNitrogenProvider.get("id").asInt());
        return Optional.of(proveedor);
    }
}
