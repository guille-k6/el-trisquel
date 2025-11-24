package trisquel.utils.Converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import trisquel.afip.model.SellCondition;

@Converter(autoApply = true)
public class SellConditionConverter implements AttributeConverter<SellCondition, Integer> {

    @Override
    public Integer convertToDatabaseColumn(SellCondition attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getId();
    }

    @Override
    public SellCondition convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return SellCondition.fromId(dbData);
    }
}
