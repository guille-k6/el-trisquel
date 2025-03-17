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
    public void addValidationError(String title, String message) {
        List<String> errorMessages = validationErrors.get(title);
        if (errorMessages == null) {
            ArrayList<String> newEntityValidations = new ArrayList<>();
            newEntityValidations.add(message);
            validationErrors.put(title, newEntityValidations);
        }
        errorMessages.add(message);
    }
}