package trisquel.Validators;

import trisquel.utils.ValidationErrorItem;

import java.util.List;

public interface Validator<T> {
    void validate(T modelElement, List<ValidationErrorItem> validationErrors);
}
