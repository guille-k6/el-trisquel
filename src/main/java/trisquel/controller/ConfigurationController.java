package trisquel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trisquel.model.ConfigurationMap;
import trisquel.service.ConfigurationService;

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
    public ResponseEntity<ConfigurationMap> getClientById(@PathVariable String key) {

        Optional<ConfigurationMap> configuration = configurationService.findByKey(key);
        if (configuration.isEmpty()) {
            return ResponseEntity.ok().build();
        }
        return configurationService.findByKey(key).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}

