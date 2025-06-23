package trisquel.afip.service;

import trisquel.afip.config.AfipConfiguration;
import trisquel.afip.model.AfipToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class AfipAuthService {

    private final AfipConfiguration config;
    private AfipToken currentToken;

    public AfipAuthService(AfipConfiguration config) {
        this.config = config;
    }

    public synchronized AfipToken getValidToken() {
        if (currentToken == null || currentToken.isExpired()) {
            currentToken = requestNewToken();
        }
        return currentToken;
    }

    private AfipToken requestNewToken() {
        try {
            //log.info("Renovando token AFIP...");
            String tra = createTRA();
            // 2. Firmar TRA con certificado
            String signedTRA = signTRA(tra);
            // 3. Enviar a WSAA
            String response = sendToWSAA(signedTRA);
            // 4. Parsear respuesta
            return parseTokenResponse(response);
        } catch (Exception e) {
            //log.error("Error obteniendo token AFIP", e);
            throw new RuntimeException("No se pudo obtener token AFIP", e);
        }
    }

    /**
     * Creates a TRA. (Ticket de requerimiento de acceso)
     *
     * @return a TRA
     */
    private String createTRA() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiration = now.plusHours(config.getTokenExpirationHours());
        // @formatter:off
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
               "<loginTicketRequest version=\"1.0\">" +
                 "<header>" +
                   "<uniqueId>" + now.toEpochSecond(ZoneOffset.UTC) + "</uniqueId>" +
                   "<generationTime>" + now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "</generationTime>" +
                   "<expirationTime>" + expiration.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "</expirationTime>" +
                 "</header>" +
                 "<service>" + config.getService() + "</service>" +
               "</loginTicketRequest>";
        // @formatter:on
    }

    private String signTRA(String tra) throws Exception {
        // TODO: Implementar firma PKCS#7 con el certificado. Esto requiere librerías como BouncyCastle. Código simplificado - necesitarías implementar la firma real
        return "signed_tra_placeholder";
    }

    private String sendToWSAA(String signedTRA) {
        // TODO: Enviar SOAP request a WSAA. Código simplificado - necesitarías implementar cliente SOAP
        return "wsaa_response_placeholder";
    }

    private AfipToken parseTokenResponse(String response) {
        // TODO: Parsear XML response de WSAA. Extraer token, sign y fecha de expiración
        return new AfipToken("token_placeholder", "sign_placeholder", LocalDateTime.now().plusHours(config.getTokenExpirationHours()));

    }
}
