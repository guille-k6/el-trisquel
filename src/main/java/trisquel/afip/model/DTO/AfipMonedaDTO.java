package trisquel.afip.model.DTO;

import trisquel.afip.model.AfipMoneda;

public record AfipMonedaDTO(String codigo, String descripcion) {
    public static AfipMonedaDTO fromEnum(AfipMoneda tipo) {
        return new AfipMonedaDTO(tipo.getCode(), tipo.getDescription());
    }
}
