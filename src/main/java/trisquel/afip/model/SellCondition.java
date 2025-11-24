package trisquel.afip.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SellCondition {
    // @formatter:off
    CONTADO(1, "Contado"),
    CUENTA_CORRIENTE(2, "Cuenta corriente"),
    TARJETA_DEBITO(3, "Tarjeta de débito"),
    TARJETA_CREDITO(4, "Tarjeta de crédito"),
    CHEQUE(5, "Cheque"), TICKET(6, "Ticket"),
    TRANSFERENCIA_BANCARIA(7, "Transferencia bancaria"),
    OTROS_MEDIOS_ELECTRONICOS(8, "Otros medios de pago electrónicos"),
    OTRA(9, "Otra");
    // @formatter:on
    private final int id;
    private final String name;

    SellCondition(int id, String name) {
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
    public static SellCondition fromId(int id) {
        for (SellCondition c : values()) {
            if (c.id == id) {
                return c;
            }
        }
        throw new IllegalArgumentException("Condición de venta desconocida (id=" + id + ")");
    }

    public static SellCondition fromName(String name) {
        for (SellCondition c : values()) {
            if (c.name.equalsIgnoreCase(name)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Condición de venta desconocida (name=" + name + ")");
    }
}
