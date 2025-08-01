package trisquel.Validators.Client;

import trisquel.Validators.Validator;
import trisquel.afip.model.AfipCondicionIva;
import trisquel.afip.model.AfipTipoDoc;
import trisquel.model.Client;
import trisquel.utils.ValidationErrorItem;

import java.util.List;

public class BasicFieldsValidator implements Validator<Client> {

    @Override
    public void validate(Client client, List<ValidationErrorItem> validationErrors) {
        validateName(client.getName(), validationErrors);
        validateAddress(client.getAddress(), validationErrors);
        validatePhoneNumber(client.getPhoneNumber(), validationErrors);
        validateDocType(client.getDocType(), validationErrors);
        validateDocNumber(client.getDocNumber(), validationErrors);
        validateIvaCondition(client.getCondicionIva(), validationErrors);
    }

    private void validateName(String name, List<ValidationErrorItem> validationErrors) {
        if (name == null || name.trim().isEmpty()) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo nombre es obligatorio"));
        } else if (name.length() > 255) {
            validationErrors.add(new ValidationErrorItem("Error", "El nombre no puede exceder 255 caracteres"));
        }
    }

    private void validateAddress(String address, List<ValidationErrorItem> validationErrors) {
        if (address == null || address.trim().isEmpty()) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo dirección es obligatorio"));
        } else if (address.length() > 255) {
            validationErrors.add(new ValidationErrorItem("Error", "La dirección no puede exceder 255 caracteres"));
        }
    }

    private void validatePhoneNumber(String phoneNumber, List<ValidationErrorItem> validationErrors) {
        if (phoneNumber != null && phoneNumber.length() > 20) {
            validationErrors.add(new ValidationErrorItem("Error", "El teléfono no puede exceder 20 caracteres"));
        }
    }

    private void validateDocType(AfipTipoDoc docType, List<ValidationErrorItem> validationErrors) {
        if (docType == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo tipo de documento es obligatorio"));
        }
    }

    private void validateDocNumber(Long docNumber, List<ValidationErrorItem> validationErrors) {
        if (docNumber == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo número de documento es obligatorio"));
        }
    }

    private void validateIvaCondition(AfipCondicionIva condicionIva, List<ValidationErrorItem> validationErrors) {
        if (condicionIva == null) {
            validationErrors.add(new ValidationErrorItem("Error", "El campo condición de IVA es obligatorio"));
        }
    }
}
