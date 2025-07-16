package trisquel.utils.Converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import trisquel.afip.model.AfipTipoDoc;

@Converter(autoApply = true)
public class AfipTipoDocConverter implements AttributeConverter<AfipTipoDoc, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AfipTipoDoc tipoDoc) {
        return tipoDoc != null ? tipoDoc.getCode() : null;
    }

    @Override
    public AfipTipoDoc convertToEntityAttribute(Integer code) {
        return code != null ? AfipTipoDoc.fromCode(code) : null;
    }
}
