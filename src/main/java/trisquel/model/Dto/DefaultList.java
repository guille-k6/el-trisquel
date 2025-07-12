package trisquel.model.Dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultList<T> {
    private final List<T> elements;
    private final T defaultValue;

    public DefaultList(List<T> elements, T defaultValue) {
        if (!elements.contains(defaultValue)) {
            throw new IllegalArgumentException("Default value must be one of the elements.");
        }
        this.elements = new ArrayList<>(elements);
        this.defaultValue = defaultValue;
    }

    public List<T> getElements() {
        return Collections.unmodifiableList(elements);
    }

    public T getDefault() {
        return defaultValue;
    }

    public void add(T element) {
        elements.add(element);
    }
}
