package trisquel.afip.model;

public enum AfipConcepto {
    PRODUCTO(1, "Productos"), SERVICIO(2, "Servicios"), PROD_Y_SERV(3, "Productos y Servicios");
    private final int code;
    private final String description;

    AfipConcepto(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static AfipConcepto fromCode(int code) {
        for (AfipConcepto tipo : values()) {
            if (tipo.code == code) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de concepto no válido: " + code);
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
