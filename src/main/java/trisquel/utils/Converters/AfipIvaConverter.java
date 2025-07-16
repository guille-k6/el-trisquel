package trisquel.utils.Converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import trisquel.afip.model.AfipIva;

@Converter(autoApply = true)
public class AfipIvaConverter implements AttributeConverter<AfipIva, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AfipIva iva) {
        return iva != null ? iva.getCode() : null;
    }

    @Override
    public AfipIva convertToEntityAttribute(Integer code) {
        return code != null ? AfipIva.fromCode(code) : null;
    }
}
