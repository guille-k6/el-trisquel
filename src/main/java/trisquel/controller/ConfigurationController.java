package trisquel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trisquel.model.ConfigurationMap;
import trisquel.service.ConfigurationService;
import trisquel.utils.ValidationException;
import trisquel.utils.ValidationExceptionResponse;

import java.util.Optional;

@RestController
@RequestMapping("/configuration")
public class ConfigurationController {
    private final ConfigurationService configurationService;
    private final ObjectMapper objectMapper;

    @Autowired
    ConfigurationController(ConfigurationService configurationService, ObjectMapper objectMapper) {
        this.configurationService = configurationService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> configuration(@PathVariable String key) {

        Optional<ConfigurationMap> configuration = configurationService.findByKey(key);
        if (configuration.isEmpty()) {
            ConfigurationMap configurationMap = new ConfigurationMap();
            configurationMap.setKey(key);
            configurationMap.setValue(objectMapper.createObjectNode());
            return ResponseEntity.ok(configurationMap);
        }
        return configurationService.findByKey(key).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody ConfigurationMap config) {
        ResponseEntity<?> response;
        try {
            configurationService.save(config);
            response = ResponseEntity.ok("");
        } catch (ValidationException e) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationExceptionResponse(e.getValidationErrors()).getErrors());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return response;
    }
}

