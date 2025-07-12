package trisquel.afip.model.DTO;

import trisquel.afip.model.AfipIva;

public record AfipIvaDTO(String nombre, int codigo, double porcentaje, String descripcion) {
    public static AfipIvaDTO fromEnum(AfipIva tipo) {
        return new AfipIvaDTO(tipo.name(), tipo.getCode(), tipo.getPercentage(), tipo.getDescription());
    }
}
