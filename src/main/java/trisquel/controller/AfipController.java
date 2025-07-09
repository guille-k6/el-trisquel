package trisquel.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trisquel.afip.auth.WSAAJavaClient;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/afip")
public class AfipController {
    WSAAJavaClient client;

    public AfipController(WSAAJavaClient client) {
        this.client = client;
    }

    @GetMapping
    public ResponseEntity authenticate() {
        try {
            String response = client.authenticate();
            System.out.println("Respuesta WSAA:");
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().build();
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
}
