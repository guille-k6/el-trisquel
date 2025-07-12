package trisquel.afip.model;

public enum AfipComprobante {
    FACT_A(1, "Factura A"), NOTA_DEBITO_A(2, "Nota Débito A"), NOTA_CREDITO_A(3, "Nota Crédito A"), RECIBO_A(4, "Recibo A"), NOTA_VENTA_CONTADO_A(5, "Nota de Venta al Contado A"), FACT_B(6, "Factura B"), NOTA_DEBITO_B(7, "Nota Débito B"), NOTA_CREDITO_B(8, "Nota Crédito B"), RECIBO_B(9, "Recibo B"), NOTA_VENTA_CONTADO_B(10, "Nota de Venta al Contado B"), FACT_C(11, "Factura C"), NOTA_DEBITO_C(12, "Nota Débito C"), NOTA_CREDITO_C(13, "Nota Crédito C"), RECIBO_C(15, "Recibo C"), NOTA_VENTA_CONTADO_C(16, "Nota de Venta al Contado C"), LIQ_SERV_PUB_A(17, "Liquidación de Servicios Públicos Clase A"), LIQ_SERV_PUB_B(18, "Liquidación de Servicios Públicos Clase B"), FACT_EXPORT(19, "Factura de Exportación"), NOTA_DEBITO_EXT(20, "Nota de Débito por Operaciones con el Exterior"), NOTA_CREDITO_EXT(21, "Nota de Crédito por Operaciones con el Exterior");

    private final int code;
    private final String description;

    AfipComprobante(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AfipComprobante fromCode(int code) {
        for (AfipComprobante tipo : values()) {
            if (tipo.code == code) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de comprobante no válido: " + code);
    }
}
