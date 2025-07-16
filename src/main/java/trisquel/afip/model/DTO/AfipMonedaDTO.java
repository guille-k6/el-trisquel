package trisquel.afip.model.DTO;

import trisquel.afip.model.AfipMoneda;

public record AfipMonedaDTO(String code, String description) {
    public static AfipMonedaDTO fromEnum(AfipMoneda tipo) {
        return new AfipMonedaDTO(tipo.getCode(), tipo.getDescription());
    }
}
