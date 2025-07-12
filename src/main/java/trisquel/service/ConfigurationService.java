package trisquel.service;

import org.springframework.stereotype.Service;
import trisquel.model.ConfigurationMap;
import trisquel.repository.ConfigurationRepository;

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
        repository.save(configurationMap);
    }
}
