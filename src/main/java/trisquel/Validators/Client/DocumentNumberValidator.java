package trisquel.Validators.Client;

import trisquel.Validators.Validator;
import trisquel.afip.model.AfipTipoDoc;
import trisquel.model.Client;
import trisquel.utils.ValidationErrorItem;

import java.util.List;

public class DocumentNumberValidator implements Validator<Client> {

    @Override
    public void validate(Client client, List<ValidationErrorItem> validationErrors) {
        if (client.getDocType() == null || client.getDocNumber() == null) {
            return; // Ya se valida en otras validaciones
        }

        validateDocumentNumber(client.getDocType(), client.getDocNumber(), validationErrors);
    }

    private void validateDocumentNumber(AfipTipoDoc tipoDoc, Long numeroDoc,
                                        List<ValidationErrorItem> validationErrors) {
        String numeroStr = String.valueOf(numeroDoc);

        switch (tipoDoc) {
            case DNI:
                validateDni(numeroStr, validationErrors);
                break;
            case CUIT:
            case CUIL:
                validateCuitCuil(numeroStr, validationErrors);
                break;
            case CDI:
                validateCedula(numeroStr, validationErrors);
                break;
            default:
                // Para tipos no especificados, no validar formato específico
                break;
        }
    }

    private void validateDni(String dni, List<ValidationErrorItem> validationErrors) {
        if (dni.length() < 7 || dni.length() > 8) {
            validationErrors.add(new ValidationErrorItem("Error", "El DNI debe tener entre 7 y 8 dígitos"));
            return;
        }

        if (!dni.matches("\\d+")) {
            validationErrors.add(new ValidationErrorItem("Error", "El DNI debe contener solo números"));
        }
    }

    private void validateCuitCuil(String cuitCuil, List<ValidationErrorItem> validationErrors) {
        if (cuitCuil.length() != 11) {
            validationErrors.add(new ValidationErrorItem("Error", "El CUIT/CUIL debe tener exactamente 11 dígitos"));
            return;
        }

        if (!cuitCuil.matches("\\d{11}")) {
            validationErrors.add(new ValidationErrorItem("Error", "El CUIT/CUIL debe contener solo números"));
        }
    }

    private void validatePasaporte(String pasaporte, List<ValidationErrorItem> validationErrors) {
        if (pasaporte.length() < 6 || pasaporte.length() > 12) {
            validationErrors.add(new ValidationErrorItem("Error", "El pasaporte debe tener entre 6 y 12 caracteres"));
            return;
        }

        // Convertir a mayúsculas para validación
        String pasaporteUpper = pasaporte.toUpperCase();
        if (!pasaporteUpper.matches("[A-Z0-9]+")) {
            validationErrors.add(new ValidationErrorItem("Error", "El pasaporte debe contener solo letras y números"));
        }
    }

    private void validateCedula(String cedula, List<ValidationErrorItem> validationErrors) {
        if (cedula.length() < 6 || cedula.length() > 10) {
            validationErrors.add(new ValidationErrorItem("Error", "La cédula debe tener entre 6 y 10 dígitos"));
            return;
        }

        if (!cedula.matches("\\d+")) {
            validationErrors.add(new ValidationErrorItem("Error", "La cédula debe contener solo números"));
        }
    }

    private void validateLibreta(String libreta, List<ValidationErrorItem> validationErrors) {
        if (libreta.length() < 6 || libreta.length() > 9) {
            validationErrors.add(new ValidationErrorItem("Error", "La libreta debe tener entre 6 y 9 dígitos"));
            return;
        }

        if (!libreta.matches("\\d+")) {
            validationErrors.add(new ValidationErrorItem("Error", "La libreta debe contener solo números"));
        }
    }
}
