package trisquel.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NitrogenProvider {
    LINDE(1, "Linde"), AIR_LIQUIDE(2, "Air Liquide");

    private final int id;
    private final String name;

    NitrogenProvider(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @JsonCreator
    public static NitrogenProvider fromId(int id) {
        for (NitrogenProvider proveedor : values()) {
            if (proveedor.id == id) {
                return proveedor;
            }
        }
        throw new IllegalArgumentException("Código de proveedor no válido: " + id);
    }
}
