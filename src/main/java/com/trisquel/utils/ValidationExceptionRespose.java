package com.trisquel.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationExceptionRespose {
    List<String> errors = new ArrayList<>();

    public ValidationExceptionRespose(HashMap<String, List<String>> validationErrors) {
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
}
