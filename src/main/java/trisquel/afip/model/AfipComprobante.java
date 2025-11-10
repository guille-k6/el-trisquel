package trisquel.afip.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AfipComprobante {
    // @formatter:off
    FACT_A(1, "Factura A", "A"),
    NOTA_DEBITO_A(2, "Nota Débito A", "A"),
    NOTA_CREDITO_A(3, "Nota Crédito A", "A"),
    RECIBO_A(4, "Recibo A", "A"),
    NOTA_VENTA_CONTADO_A(5, "Nota de Venta al Contado A", "A"),
    FACT_B(6, "Factura B", "B"),
    NOTA_DEBITO_B(7, "Nota Débito B", "B"),
    NOTA_CREDITO_B(8, "Nota Crédito B", "B"),
    RECIBO_B(9, "Recibo B", "B"),
    NOTA_VENTA_CONTADO_B(10, "Nota de Venta al Contado B", "B"),
    FACT_C(11, "Factura C", "C"),
    NOTA_DEBITO_C(12, "Nota Débito C", "C"),
    NOTA_CREDITO_C(13, "Nota Crédito C", "C"),
    RECIBO_C(15, "Recibo C", "C"),
    NOTA_VENTA_CONTADO_C(16, "Nota de Venta al Contado C", "C"),
    LIQ_SERV_PUB_A(17, "Liquidación de Servicios Públicos Clase A", "A"),
    LIQ_SERV_PUB_B(18, "Liquidación de Servicios Públicos Clase B", "B"),
    FACT_EXPORT(19, "Factura de Exportación", "X"),
    NOTA_DEBITO_EXT(20, "Nota de Débito por Operaciones con el Exterior", "X"),
    NOTA_CREDITO_EXT(21, "Nota de Crédito por Operaciones con el Exterior", "X");
    // @formatter:on
    private final int code;
    private final String description;
    private final String letter;

    AfipComprobante(int code, String description, String letter) {
        this.code = code;
        this.description = description;
        this.letter = letter;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getLetter() {
        return letter;
    }

    @JsonCreator
    public static AfipComprobante fromCode(int code) {
        for (AfipComprobante tipo : values()) {
            if (tipo.code == code) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de comprobante no válido: " + code);
    }
}
