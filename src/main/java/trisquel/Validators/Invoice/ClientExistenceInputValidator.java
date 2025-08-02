package trisquel.Validators.Invoice;

import trisquel.Validators.Validator;
import trisquel.model.Client;
import trisquel.model.Dto.InvoiceInputDTO;
import trisquel.repository.ClientRepository;
import trisquel.utils.ValidationErrorItem;

import java.util.List;
import java.util.Optional;

public class ClientExistenceInputValidator implements Validator<InvoiceInputDTO> {

    private final ClientRepository clientRepository;

    public ClientExistenceInputValidator(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public void validate(InvoiceInputDTO invoiceInputDTO, List<ValidationErrorItem> validationErrors) {
        if (invoiceInputDTO.getClientId() != null) {
            validateClientExists(invoiceInputDTO.getClientId(), validationErrors);
        }
    }

    private void validateClientExists(Long clientId, List<ValidationErrorItem> validationErrors) {
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isEmpty()) {
            validationErrors.add(new ValidationErrorItem("Error", "No se encontró un cliente con el id: " + clientId));
        }
    }
}
