package trisquel.utils.Converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import trisquel.afip.model.AfipMoneda;

@Converter(autoApply = true)
public class AfipMonedaConverter implements AttributeConverter<AfipMoneda, String> {

    @Override
    public String convertToDatabaseColumn(AfipMoneda moneda) {
        return moneda != null ? moneda.getCode() : null;
    }

    @Override
    public AfipMoneda convertToEntityAttribute(String code) {
        return code != null ? AfipMoneda.fromCode(code) : null;
    }
}
