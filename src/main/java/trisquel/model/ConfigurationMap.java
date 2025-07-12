package trisquel.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import trisquel.utils.JsonNodeConverter;

@Entity
@Table(name = "configuration_map")
public class ConfigurationMap {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "configuration_seq")
    @SequenceGenerator(name = "configuration_seq", sequenceName = "configuration_seq", allocationSize = 1)
    private Long id;
    private String key;
    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public JsonNode getValue() {
        return value;
    }

    public void setValue(JsonNode value) {
        this.value = value;
    }
}
