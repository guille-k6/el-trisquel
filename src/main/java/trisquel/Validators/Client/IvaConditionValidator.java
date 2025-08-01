package trisquel.Validators.Client;

import trisquel.Validators.Validator;
import trisquel.afip.model.AfipCondicionIva;
import trisquel.afip.model.AfipTipoDoc;
import trisquel.model.Client;
import trisquel.utils.ValidationErrorItem;

import java.util.Arrays;
import java.util.List;

public class IvaConditionValidator implements Validator<Client> {

    @Override
    public void validate(Client client, List<ValidationErrorItem> validationErrors) {
        if (client.getDocType() == null || client.getCondicionIva() == null) {
            return; // Ya se valida en otras validaciones
        }

        validateIvaCondition(client.getDocType(), client.getCondicionIva(), validationErrors);
    }

    private void validateIvaCondition(AfipTipoDoc tipoDoc, AfipCondicionIva condicionIva,
                                      List<ValidationErrorItem> validationErrors) {
        switch (tipoDoc) {
            case CUIT:
                validateIvaForCuit(condicionIva, validationErrors);
                break;
            case CUIL:
                validateIvaForCuil(condicionIva, validationErrors);
                break;
            case DNI:
                validateIvaForDni(condicionIva, validationErrors);
                break;
            case CDI:
                validateIvaForForeignDoc(condicionIva, validationErrors);
                break;
            default:
                // Para otros tipos, permitir cualquier condición
                break;
        }
    }

    private void validateIvaForCuit(AfipCondicionIva condicionIva, List<ValidationErrorItem> validationErrors) {
        // Con CUIT puede ser cualquier condición excepto consumidor final
        if (condicionIva == AfipCondicionIva.CONSUMIDOR_FINAL) {
            validationErrors.add(new ValidationErrorItem("Error", "Un cliente con CUIT no puede ser Consumidor Final"));
        }
    }

    private void validateIvaForCuil(AfipCondicionIva condicionIva, List<ValidationErrorItem> validationErrors) {
        // Con CUIL típicamente monotributista, exento o responsable inscripto
        if (!Arrays.asList(AfipCondicionIva.MONOTRIBUTO, AfipCondicionIva.SUJETO_EXENTO, AfipCondicionIva.RESPONSABLE_INSCRIPTO).contains(condicionIva)) {
            validationErrors.add(new ValidationErrorItem("Error", "Un cliente con CUIL debe ser Monotributista, Exento o Responsable Inscripto"));
        }
    }

    private void validateIvaForDni(AfipCondicionIva condicionIva, List<ValidationErrorItem> validationErrors) {
        // Con DNI típicamente consumidor final, monotributista o exento
        if (!Arrays.asList(AfipCondicionIva.CONSUMIDOR_FINAL, AfipCondicionIva.MONOTRIBUTO, AfipCondicionIva.SUJETO_EXENTO).contains(condicionIva)) {
            validationErrors.add(new ValidationErrorItem("Error", "Un cliente con DNI debe ser Consumidor Final, Monotributista o Exento"));
        }
    }

    private void validateIvaForForeignDoc(AfipCondicionIva condicionIva, List<ValidationErrorItem> validationErrors) {
        // Documentos extranjeros, típicamente consumidor final o exento
        if (!Arrays.asList(AfipCondicionIva.CONSUMIDOR_FINAL, AfipCondicionIva.SUJETO_EXENTO).contains(condicionIva)) {
            validationErrors.add(new ValidationErrorItem("Error", "Un cliente con documento extranjero debe ser Consumidor Final o Exento"));
        }
    }

    private void validateIvaForLibreta(AfipCondicionIva condicionIva, List<ValidationErrorItem> validationErrors) {
        // Libretas, similar a DNI
        if (!Arrays.asList(AfipCondicionIva.CONSUMIDOR_FINAL, AfipCondicionIva.MONOTRIBUTO, AfipCondicionIva.SUJETO_EXENTO).contains(condicionIva)) {
            validationErrors.add(new ValidationErrorItem("Error", "Un cliente con libreta debe ser Consumidor Final, Monotributista o Exento"));
        }
    }
}