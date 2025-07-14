package trisquel.controller;

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

    @Autowired
    ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping("/{key}")
    public ResponseEntity<ConfigurationMap> configuration(@PathVariable String key) {

        Optional<ConfigurationMap> configuration = configurationService.findByKey(key);
        if (configuration.isEmpty()) {
            return ResponseEntity.ok().build();
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return response;
    }
}

