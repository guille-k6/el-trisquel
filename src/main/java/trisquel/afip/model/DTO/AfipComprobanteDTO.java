package trisquel.afip.model.DTO;

import trisquel.afip.model.AfipComprobante;

public record AfipComprobanteDTO(String nombre, int codigo, String descripcion) {
    public static AfipComprobanteDTO fromEnum(AfipComprobante tipo) {
        return new AfipComprobanteDTO(tipo.name(), tipo.getCode(), tipo.getDescription());
    }
}
