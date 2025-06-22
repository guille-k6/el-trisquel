package com.trisquel.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ValidationException extends RuntimeException {

    /**
     * Map consisting of a key item and its errors. Example: DailyBookItem 1: The date cannot be null, the vehicle cannot be null, the client cannot be null.
     */
    private HashMap<String, List<String>> validationErrors = new HashMap<>();

    public ValidationException() {
    }

    public HashMap<String, List<String>> getValidationErrors() {
        return validationErrors;
    }

    /**
     * @param title
     * @param message
     */
    public ValidationException addValidationError(String title, String message) {
        List<String> errorMessages = validationErrors.get(title);
        if (errorMessages == null) {
            ArrayList<String> newEntityValidations = new ArrayList<>();
            newEntityValidations.add(message);
            validationErrors.put(title, newEntityValidations);
        } else {
            errorMessages.add(message);
        }
        return this;
    }

    public static void verifyAndMaybeThrowValidationException(List<ValidationErrorItem> validationErrorList) {
        if (!validationErrorList.isEmpty()) {
            ValidationException validationException = new ValidationException();
            for (ValidationErrorItem validationErrorItem : validationErrorList) {
                validationException.addValidationError(validationErrorItem.title(), validationErrorItem.message());
            }
            throw validationException;
        }
    }

    public ValidationException getValidationException() {
        return this;
    }
}