package trisquel.utils.Converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import trisquel.afip.model.AfipConcepto;
import trisquel.afip.model.AfipCondicionIva;

@Converter(autoApply = true)
public class AfipCondicionIvaConverter implements AttributeConverter<AfipCondicionIva, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AfipCondicionIva condicionIva) {
        return condicionIva != null ? condicionIva.getCode() : null;
    }

    @Override
    public AfipCondicionIva convertToEntityAttribute(Integer code) {
        return code != null ? AfipCondicionIva.fromCode(code) : null;
    }
}
