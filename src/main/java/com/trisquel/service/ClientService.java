package com.trisquel.service;

import com.trisquel.model.Client;
import com.trisquel.repository.ClientRepository;
import com.trisquel.utils.ValidationErrorItem;
import com.trisquel.utils.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    @Autowired
    ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    private final ClientRepository repository;

    public List<Client> findAll() {
        return repository.findAll();
    }

    public Optional<Client> findById(Long id) {
        return repository.findById(id);
    }

    public Client save(Client client) {
        validateClient(client);
        return repository.save(client);
    }

    private void validateClient(Client client) {
        List<ValidationErrorItem> validationErrors = new ArrayList<>();
        if (client.getId() == null || client.getId() == 0) {
            client.setId(null);
        } else {
            // This is an update, verify the entity exists
            Optional<Client> existingClient = repository.findById(client.getId());
            if (existingClient.isEmpty()) {
                ValidationException validationException = new ValidationException();
                validationException.addValidationError("Error", "Cliente no encontrado");
                throw validationException;
            }
        }
        if (client.getName().isBlank()) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo nombre es obligatorio"));
        }
        if (client.getAddress() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo dirección es obligatorio"));
        }
        if (client.getPhoneNumber() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo número de teléfono es obligatorio"));
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
