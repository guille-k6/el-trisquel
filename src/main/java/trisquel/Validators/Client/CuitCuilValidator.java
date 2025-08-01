package trisquel.Validators.Client;

import trisquel.Validators.Validator;
import trisquel.afip.model.AfipTipoDoc;
import trisquel.model.Client;
import trisquel.utils.ValidationErrorItem;

import java.util.List;

public class CuitCuilValidator implements Validator<Client> {

    @Override
    public void validate(Client client, List<ValidationErrorItem> validationErrors) {
        if (client.getDocType() == null || client.getDocNumber() == null) {
            return; // Ya se valida en otras validaciones
        }

        if (client.getDocType() == AfipTipoDoc.CUIT || client.getDocType() == AfipTipoDoc.CUIL) {
            validateCuitCuil(client.getDocNumber(), validationErrors);
        }
    }

    private void validateCuitCuil(Long cuitCuil, List<ValidationErrorItem> validationErrors) {
        String cuitStr = String.valueOf(cuitCuil);

        // Debe tener exactamente 11 dígitos
        if (cuitStr.length() != 11) {
            validationErrors.add(new ValidationErrorItem("Error", "El CUIT/CUIL debe tener exactamente 11 dígitos"));
            return;
        }

        // Validar que sean solo números
        if (!cuitStr.matches("\\d{11}")) {
            validationErrors.add(new ValidationErrorItem("Error", "El CUIT/CUIL debe contener solo números"));
            return;
        }

        // Validar dígito verificador
        if (!isValidCuitCheckDigit(cuitStr)) {
            validationErrors.add(new ValidationErrorItem("Error", "El CUIT/CUIL ingresado no es válido (dígito verificador incorrecto)"));
        }
    }

    private boolean isValidCuitCheckDigit(String cuit) {
        int[] multipliers = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
        int sum = 0;

        // Calcular suma ponderada de los primeros 10 dígitos
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cuit.charAt(i)) * multipliers[i];
        }

        int remainder = sum % 11;
        int checkDigit = 11 - remainder;

        // Casos especiales
        if (checkDigit == 11) {
            checkDigit = 0;
        } else if (checkDigit == 10) {
            checkDigit = 9;
        }

        return checkDigit == Character.getNumericValue(cuit.charAt(10));
    }
}
