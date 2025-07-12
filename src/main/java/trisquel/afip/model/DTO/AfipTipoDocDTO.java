package trisquel.afip.model.DTO;

import trisquel.afip.model.AfipConcepto;
import trisquel.afip.model.AfipTipoDoc;

public record AfipTipoDocDTO(String nombre, int codigo, String descripcion) {
    public static AfipTipoDocDTO fromEnum(AfipTipoDoc tipo) {
        return new AfipTipoDocDTO(tipo.name(), tipo.getCode(), tipo.getDescription());
    }
}
