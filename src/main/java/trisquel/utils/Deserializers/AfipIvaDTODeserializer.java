package trisquel.utils.Deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import trisquel.afip.model.AfipIva;
import trisquel.afip.model.DTO.AfipIvaDTO;

import java.io.IOException;

public class AfipIvaDTODeserializer extends JsonDeserializer<AfipIvaDTO> {
    @Override
    public AfipIvaDTO deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        if (node.isInt()) {
            int code = node.asInt();
            AfipIva enumValue = AfipIva.fromCode(code);
            return AfipIvaDTO.fromEnum(enumValue);
        } else {
            int code = node.get("code").asInt();
            double percentage = node.get("percentage").asDouble();
            String description = node.get("description").asText();
            return new AfipIvaDTO(code, percentage, description);
        }
    }
}
