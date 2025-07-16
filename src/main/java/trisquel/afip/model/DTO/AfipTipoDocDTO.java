package trisquel.afip.model.DTO;

import trisquel.afip.model.AfipTipoDoc;

public record AfipTipoDocDTO(int code, String description) {
    public static AfipTipoDocDTO fromEnum(AfipTipoDoc tipo) {
        return new AfipTipoDocDTO(tipo.getCode(), tipo.getDescription());
    }
}
