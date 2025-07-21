package trisquel.afip.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AfipCondicionIva {
    RESPONSABLE_INSCRIPTO(1, "IVA Responsable Inscripto", "A/M/C"), MONOTRIBUTO(6, "Responsable Monotributo", "A/M/C"), MONOTRIBUTO_SOCIAL(13, "Monotributista Social", "A/M/C"), MONOTRIBUTO_TRABAJADOR_INDEPENDIENTE(16, "Monotributo Trabajador Independiente Promovido", "A/M/C"), SUJETO_EXENTO(4, "IVA Sujeto Exento", "B/C"), CONSUMIDOR_FINAL(5, "Consumidor Final", "B/C"), SUJETO_NO_CATEGORIZADO(7, "Sujeto No Categorizado", "B/C"), PROVEEDOR_EXTERIOR(8, "Proveedor del Exterior", "B/C"), CLIENTE_EXTERIOR(9, "Cliente del Exterior", "B/C"), IVA_LIBERADO(10, "IVA Liberado – Ley N° 19.640", "B/C"), IVA_NO_ALCANZADO(15, "IVA No Alcanzado", "B/C");

    private final int code;
    private final String description;
    private final String comprobantesClase;

    AfipCondicionIva(int id, String description, String comprobantesClase) {
        this.code = id;
        this.description = description;
        this.comprobantesClase = comprobantesClase;
    }

    @JsonCreator
    public static AfipCondicionIva fromCode(int id) {
        for (AfipCondicionIva condicion : values()) {
            if (condicion.code == id) {
                return condicion;
            }
        }
        throw new IllegalArgumentException("ID de condición IVA no válido: " + id);
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getComprobantesClase() {
        return comprobantesClase;
    }
}
