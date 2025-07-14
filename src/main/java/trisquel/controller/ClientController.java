package trisquel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trisquel.model.Client;
import trisquel.service.ClientService;
import trisquel.utils.ValidationException;
import trisquel.utils.ValidationExceptionResponse;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<Client> getAllClients() {
        return clientService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        return clientService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createVehicle(@RequestBody Client client) {
        ResponseEntity<?> response;
        try {
            clientService.save(client);
            response = ResponseEntity.ok("");
        } catch (ValidationException e) {
            response = ResponseEntity.status(HttpStatus.CONFLICT).body(new ValidationExceptionResponse(e.getValidationErrors()).getErrors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return response;
    }

    @Deprecated
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@RequestBody Client client) {
        return ResponseEntity.ok(clientService.save(client));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
