package trisquel.afip.model;

public enum AfipIva {
    IVA_0p(3, 0.0, "IVA 0%"), IVA_105p(4, 10.5, "IVA 10.5%"), IVA_21p(5, 21.0, "IVA 21%"), IVA_27p(6, 27.0, "IVA 27%"), IVA_5p(8, 5.0, "IVA 5%"), IVA_250p(9, 2.5, "IVA 2.50%");

    private final int code;
    private final double percentage;
    private final String description;

    AfipIva(int code, double percentage, String description) {
        this.code = code;
        this.percentage = percentage;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public double getPercentage() {
        return percentage;
    }

    public String getDescription() {
        return description;
    }

    public static AfipIva fromCodigo(int code) {
        for (AfipIva tipo : values()) {
            if (tipo.code == code) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de IVA no válido: " + code);
    }

    public static AfipIva fromPorcentaje(double percentage) {
        for (AfipIva tipo : values()) {
            if (Double.compare(tipo.percentage, percentage) == 0) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Porcentaje de IVA no válido: " + percentage);
    }
}
