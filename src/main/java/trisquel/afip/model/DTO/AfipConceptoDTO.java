package trisquel.afip.model.DTO;

import trisquel.afip.model.AfipConcepto;

public record AfipConceptoDTO(int code, String description) {
    public static AfipConceptoDTO fromEnum(AfipConcepto tipo) {
        return new AfipConceptoDTO(tipo.getCode(), tipo.getDescription());
    }
}
