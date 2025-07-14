package trisquel.afip.model;

public enum AfipTipoDoc {
    CUIT(80, "CUIT"), CUIL(86, "CUIL"), CDI(87, "CDI"), DNI(96, "DNI"), NN(0, "Sin Identificar");
    private final int code;
    private final String description;

    AfipTipoDoc(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static AfipTipoDoc fromCode(int code) {
        for (AfipTipoDoc tipo : values()) {
            if (tipo.code == code) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de documento no válido: " + code);
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
