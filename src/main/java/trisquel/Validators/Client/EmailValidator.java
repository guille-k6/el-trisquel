package trisquel.Validators.Client;

import trisquel.Validators.Validator;
import trisquel.model.Client;
import trisquel.utils.ValidationErrorItem;

import java.util.List;
import java.util.regex.Pattern;

public class EmailValidator implements Validator<Client> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    @Override
    public void validate(Client client, List<ValidationErrorItem> validationErrors) {
        String email = client.getEmail();

        // Email es opcional, pero si está presente debe ser válido
        if (email != null && !email.trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
                validationErrors.add(new ValidationErrorItem("Error", "El formato del email no es válido"));
            }

            if (email.length() > 255) {
                validationErrors.add(new ValidationErrorItem("Error", "El email no puede exceder 255 caracteres"));
            }
        }
    }
}
