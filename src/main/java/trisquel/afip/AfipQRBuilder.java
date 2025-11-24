package trisquel.afip;

import com.fasterxml.jackson.databind.ObjectMapper;
import trisquel.afip.model.AfipTipoDoc;
import trisquel.model.ConfigurationMap;
import trisquel.model.Invoice;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

public class AfipQRBuilder {

    // URL base oficial para el QR
    private static final String AFIP_QR_BASE_URL = "https://www.arca.gob.ar/fe/qr/?p=";

    private final ObjectMapper objectMapper;

    public AfipQRBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Construye la URL completa que debe ir codificada en el código QR
     * a partir de la factura y los datos de organización.
     */
    public String buildQrUrl(Invoice invoice, ConfigurationMap orgInfo) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();

            // ===== Campos obligatorios AFIP/ARCA =====
            data.put("ver", 1); // versión de formato

            data.put("fecha", invoice.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));

            // CUIT emisor desde configuración
            long cuitEmisor = orgInfo.getValueAsLong("cuit");
            data.put("cuit", cuitEmisor);

            // Punto de venta (int)
            data.put("ptoVta", invoice.getSellPoint().intValue());

            // Tipo de comprobante: tu enum AfipComprobante ya tiene el código AFIP
            data.put("tipoCmp", invoice.getComprobante().getCode());

            // Número de comprobante
            data.put("nroCmp", invoice.getNumero());

            // Importe total
            data.put("importe", invoice.getTotal());

            // Moneda (código AFIP, ej. PES)
            data.put("moneda", invoice.getMoneda().getCode());

            // Cotización: 1 si es PES
            data.put("ctz", java.math.BigDecimal.ONE);

            // ===== Datos del receptor (opcionales) =====
            if (invoice.getClient() != null && invoice.getClient().getDocType() != null && invoice.getClient().getDocNumber() != null && invoice.getClient().getDocType() != AfipTipoDoc.NN) {

                AfipTipoDoc docType = invoice.getClient().getDocType();
                data.put("tipoDocRec", docType.getCode());

                long nroDocRec = Long.parseLong(invoice.getClient().getDocNumber().toString());
                data.put("nroDocRec", nroDocRec);
            }

            // ===== Autorización (CAE) =====
            data.put("tipoCodAut", "E"); // "E" = CAE, "A" = CAEA

            long codAut = Long.parseLong(invoice.getCae());
            data.put("codAut", codAut);

            // ===== JSON -> Base64 -> URL =====
            String json = objectMapper.writeValueAsString(data);

            String base64 = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));

            return AFIP_QR_BASE_URL + base64;

        } catch (Exception e) {
            throw new RuntimeException("Error generando URL de QR AFIP", e);
        }
    }
}
