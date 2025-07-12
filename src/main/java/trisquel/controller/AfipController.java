package trisquel.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trisquel.afip.auth.AfipAuthService;
import trisquel.afip.model.AfipAuth;
import trisquel.afip.model.AfipComprobante;
import trisquel.afip.model.DTO.AfipComprobanteDTO;
import trisquel.model.Dto.DefaultList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/afip")
public class AfipController {
    AfipAuthService client;

    public AfipController(AfipAuthService client) {
        this.client = client;
    }

    @GetMapping
    public ResponseEntity authenticate() throws Exception {
        AfipAuth response = null;
        try {
            response = client.authenticate();
            if (response.getErrorMessage() == null) {
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.getErrorMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/debug-resources")
    public ResponseEntity<?> debugResources() {
        try {
            //            Resource certResource = resourceLoader.getResource("classpath:certificates/certificado.crt");
            //            Resource keyResource = resourceLoader.getResource("classpath:certificates/trisquelPrivKey.key");

            Map<String, Object> debug = new HashMap<>();
            //            debug.put("certExists", certResource.exists());
            //            debug.put("keyExists", keyResource.exists());
            //            debug.put("certPath", certResource.getURI().toString());
            //            debug.put("keyPath", keyResource.getURI().toString());

            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/tipo-comprobante")
    public ResponseEntity<DefaultList<?>> tipoCompobante() {
        List<AfipComprobanteDTO> vouchers = Arrays.stream(AfipComprobante.values()).map(AfipComprobanteDTO::fromEnum).toList();
        AfipComprobanteDTO comprobanteDefault = AfipComprobanteDTO.fromEnum(AfipComprobante.FACT_A);
        DefaultList<AfipComprobanteDTO> defaultList = new DefaultList<>(vouchers, comprobanteDefault);
        return ResponseEntity.ok(defaultList);

    }
}
