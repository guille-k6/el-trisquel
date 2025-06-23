package trisquel.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationExceptionResponse {
    private List<String> errors = new ArrayList<>();
    private Map<String, List<String>> validationErrors;

    public ValidationExceptionResponse(HashMap<String, List<String>> validationErrors) {
        this.validationErrors = validationErrors;

        for (Map.Entry<String, List<String>> entry : validationErrors.entrySet()) {
            String errorTitle = entry.getKey();
            List<String> entryErrors = entry.getValue();
            StringBuilder errorBody = new StringBuilder();
            for (String error : entryErrors) {
                errorBody.append(error);
                errorBody.append(". ");
            }
            errors.add(errorTitle + ": " + errorBody);
        }
    }

    public List<String> getErrors() {
        return errors;
    }

    public Map<String, List<String>> getValidationErrors() {
        return validationErrors;
    }
}
