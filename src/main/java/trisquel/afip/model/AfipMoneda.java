package trisquel.afip.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AfipMoneda {
    PESO("PES", "PESO ARGENTINO"), DOLAR("DOL", "DOLAR ESTADOUNIDENSE"), REAL("12", "REAL"), PESO_UY("11", "PESO URUGUAYO");

    private final String code;
    private final String description;

    AfipMoneda(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static AfipMoneda fromCode(String code) {
        for (AfipMoneda tipo : values()) {
            if (tipo.code.equals(code)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de comprobante no válido: " + code);
    }
}
