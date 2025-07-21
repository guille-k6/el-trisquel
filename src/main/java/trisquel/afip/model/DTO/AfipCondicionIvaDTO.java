package trisquel.afip.model.DTO;

import trisquel.afip.model.AfipCondicionIva;

public record AfipCondicionIvaDTO(int code, String description, String comprobantesClase) {
    public static AfipCondicionIvaDTO fromEnum(AfipCondicionIva condicion) {
        return new AfipCondicionIvaDTO(condicion.getCode(), condicion.getDescription(), condicion.getComprobantesClase());
    }
}
