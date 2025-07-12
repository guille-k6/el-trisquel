package trisquel.afip.model.DTO;

import trisquel.afip.model.AfipConcepto;

public record AfipConceptoDTO(String nombre, int codigo, String descripcion) {
    public static AfipConceptoDTO fromEnum(AfipConcepto tipo) {
        return new AfipConceptoDTO(tipo.name(), tipo.getCode(), tipo.getDescription());
    }
}
