package trisquel.model;

public enum NitrogenProvider {
    LINDE("Linde"), AIR_LIQUIDE("Air Liquide");

    private final String value;

    NitrogenProvider(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
