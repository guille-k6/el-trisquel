package trisquel.afip.model.DTO;

import trisquel.afip.model.AfipIva;

public record AfipIvaDTO(int code, double percentage, String description) {
    public static AfipIvaDTO fromEnum(AfipIva tipo) {
        return new AfipIvaDTO(tipo.getCode(), tipo.getPercentage(), tipo.getDescription());
    }
}
