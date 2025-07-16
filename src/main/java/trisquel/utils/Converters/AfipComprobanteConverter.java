package trisquel.utils.Converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import trisquel.afip.model.AfipComprobante;

@Converter(autoApply = true)
public class AfipComprobanteConverter implements AttributeConverter<AfipComprobante, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AfipComprobante comprobante) {
        return comprobante != null ? comprobante.getCode() : null;
    }

    @Override
    public AfipComprobante convertToEntityAttribute(Integer code) {
        return code != null ? AfipComprobante.fromCode(code) : null;
    }
}