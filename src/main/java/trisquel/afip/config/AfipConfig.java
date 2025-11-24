package trisquel.afip.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import trisquel.afip.AfipQRBuilder;

@Configuration
public class AfipConfig {

    @Bean
    public AfipQRBuilder afipQRBuilder(ObjectMapper objectMapper) {
        return new AfipQRBuilder(objectMapper);
    }
}