package trisquel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trisquel.Validators.Client.*;
import trisquel.Validators.Validator;
import trisquel.model.Client;
import trisquel.model.DailyBookItem;
import trisquel.model.Dto.ClientDTO;
import trisquel.repository.ClientRepository;
import trisquel.repository.DailyBookItemRepository;
import trisquel.utils.ValidationErrorItem;
import trisquel.utils.ValidationException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClientService {
    @Autowired
    ClientService(ClientRepository repository, DailyBookItemRepository dailyBookItemRepository) {
        this.repository = repository;
        this.dailyBookItemRepository = dailyBookItemRepository;
    }

    private final ClientRepository repository;
    private final DailyBookItemRepository dailyBookItemRepository;

    public List<ClientDTO> findAll() {
        List<Client> clients = repository.findAll();
        return ClientDTO.translateToDTOs(clients);
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
        List<Validator<Client>> validators = Arrays.asList(new BasicFieldsValidator(), new DocumentNumberValidator(), new CuitCuilValidator(), new IvaConditionValidator(), new EmailValidator());
        for (Validator<Client> validator : validators) {
            validator.validate(client, validationErrors);
            // Verificar y lanzar excepción si hay errores
            ValidationException.verifyAndMaybeThrowValidationException(validationErrors);
        }
    }

    public void delete(Long id) {
        List<DailyBookItem> itemsReferred = dailyBookItemRepository.findByClient(id);
        if (!itemsReferred.isEmpty()) {
            Set<Long> idsReferred = itemsReferred.stream().map(dbi -> dbi.getDailyBook().getId()).collect(Collectors.toSet());
            ValidationException validationException = new ValidationException();
            validationException.addValidationError("Error", "El cliente está siendo referenciado en los libros diarios: " + idsReferred);
            throw validationException;
        }
        repository.deleteById(id);
    }
}
