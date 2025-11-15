package trisquel.utils.Converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import trisquel.model.NitrogenProvider;


@Converter(autoApply = true)
public class NitrogenProviderConverter implements AttributeConverter<NitrogenProvider, Integer> {

    @Override
    public Integer convertToDatabaseColumn(NitrogenProvider provider) {
        return provider != null ? provider.getId() : null;
    }

    @Override
    public NitrogenProvider convertToEntityAttribute(Integer id) {
        return id != null ? NitrogenProvider.fromId(id) : null;
    }
}
