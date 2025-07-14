package trisquel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trisquel.model.Client;
import trisquel.repository.ClientRepository;
import trisquel.utils.ValidationErrorItem;
import trisquel.utils.ValidationException;

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
        //        if (client.getAddress() == null) {
        //            validationErrors.add(new ValidationErrorItem("Error", "El campo dirección es obligatorio"));
        //        }
        //        if (client.getPhoneNumber() == null) {
        //            validationErrors.add(new ValidationErrorItem("Error", "El campo número de teléfono es obligatorio"));
        //        }
        if (client.getDocType() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo tipo de documento es obligatorio"));
        }
        if (client.getDocNumber() == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo número de documento es obligatorio"));
        }
        //        if(client.getEmail() == null) {
        //            validationErrors.add(new ValidationErrorItem("Error", "El campo email es obligatorio"));
        //        }
        ValidationException.verifyAndMaybeThrowValidationException(validationErrors);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
