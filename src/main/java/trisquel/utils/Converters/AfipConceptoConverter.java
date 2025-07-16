package trisquel.utils.Converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import trisquel.afip.model.AfipConcepto;

@Converter(autoApply = true)
public class AfipConceptoConverter implements AttributeConverter<AfipConcepto, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AfipConcepto concepto) {
        return concepto != null ? concepto.getCode() : null;
    }

    @Override
    public AfipConcepto convertToEntityAttribute(Integer code) {
        return code != null ? AfipConcepto.fromCode(code) : null;
    }
}
