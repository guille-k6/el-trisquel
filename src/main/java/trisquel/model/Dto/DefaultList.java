package trisquel.model.Dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

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

    /**
     * Retorna una DefaultList del enumerado defaultEnum
     *
     * @param defaultEnum valor default de enumerado que va a tener la lista
     * @param mapper      funcion fromEnum de las clase DTO del enumerado
     * @return una DefaultList del DTO del enumerado con valor default defaultEnum
     */
    public static <E extends Enum<E>, D> DefaultList<D> buildDefaultListFromEnum(E defaultEnum, Function<E, D> mapper) {
        Class<E> enumClass = defaultEnum.getDeclaringClass();
        List<D> items = Arrays.stream(enumClass.getEnumConstants()).map(mapper).toList();
        D defaultItem = mapper.apply(defaultEnum);
        return new DefaultList<>(items, defaultItem);
    }
}
