package trisquel.afip.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trisquel.afip.auth.AfipAuthService;
import trisquel.afip.config.AfipConfiguration;
import trisquel.afip.model.AfipAuth;
import trisquel.afip.model.AfipInvoiceRequest;
import trisquel.afip.model.AfipInvoiceResponse;

import java.time.LocalDate;

// Servicio principal WSFEv1
@Service
public class AfipInvoiceService {

    private final AfipConfiguration config;
    private final AfipAuthService authService;

    @Autowired
    public AfipInvoiceService(AfipConfiguration config, AfipAuthService authService) {
        this.config = config;
        this.authService = authService;
    }

    public AfipInvoiceResponse authorizeInvoice(AfipInvoiceRequest request) {
        try {
            // log.info("Autorizando factura en AFIP...");
            // 1. Obtener token válido
            // AfipToken token = authService.getValidToken();
            // 2. Obtener próximo número de comprobante
            //long nextInvoiceNumber = getNextInvoiceNumber(token);
            // request.setInvoiceNumber(nextInvoiceNumber);
            // 3. Crear SOAP request para FECAESolicitar
            // String soapRequest = buildFECAESolicitarRequest(token, request);
            // 4. Enviar a WSFEv1
            // String soapResponse = sendSOAPRequest(soapRequest);
            // 5. Parsear respuesta
            // return parseFECAEResponse(soapResponse);
            return null;
        } catch (Exception e) {
            // log.error("Error autorizando factura en AFIP", e);
            throw new RuntimeException("Error en autorización AFIP", e);
        }
    }

    private long getNextInvoiceNumber(AfipAuth token) {
        // Llamar a FECompUltimoAutorizado para obtener el último número
        // @formatter:off
        String soapRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ar=\"http://ar.gov.afip.dif.FEV1/\">" +
                "<soap:Header/>" +
                "<soap:Body>" +
                "<ar:FECompUltimoAutorizado>" +
                "<ar:Auth>" +
                "<ar:Token>" + token.getToken() + "</ar:Token>" +
                "<ar:Sign>" + token.getSign() + "</ar:Sign>" +
                "<ar:Cuit>" + config.getCuit() + "</ar:Cuit>" +
                "</ar:Auth>" +
                "<ar:PtoVta>1</ar:PtoVta>" +
                "<ar:CbteTipo>11</ar:CbteTipo>" + // 11 = Factura C
                "</ar:FECompUltimoAutorizado>" +
                "</soap:Body>" +
                "</soap:Envelope>";
        // @formatter:on
        // Enviar y parsear respuesta
        // Retornar último número + 1
        return 1L; // Placeholder
    }

    private String buildFECAESolicitarRequest(AfipAuth token, AfipInvoiceRequest request) {
        // @formatter:off
        /*
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ar=\"http://ar.gov.afip.dif.FEV1/\">" +
                "<soap:Header/>" +
                "<soap:Body>" +
                "<ar:FECAESolicitar>" +
                "<ar:Auth>" +
                "<ar:Token>" + token.getToken() + "</ar:Token>" +
                "<ar:Sign>" + token.getSign() + "</ar:Sign>" +
                "<ar:Cuit>" + config.getCuit() + "</ar:Cuit>" +
                "</ar:Auth>" +
                "<ar:FeCAEReq>" +
                "<ar:FeCabReq>" +
                "<ar:CantReg>1</ar:CantReg>" +
                "<ar:PtoVta>1</ar:PtoVta>" +
                "<ar:CbteTipo>11</ar:CbteTipo>" + // Factura C
                "</ar:FeCabReq>" +
                "<ar:FeDetReq>" +
                "<ar:FECAEDetRequest>" +
                "<ar:Concepto>" + request.getConceptType() + "</ar:Concepto>" +
                "<ar:DocTipo>" + request.getDocumentType() + "</ar:DocTipo>" +
                "<ar:DocNro>" + request.getDocumentNumber() + "</ar:DocNro>" +
                "<ar:CbteDesde>" + request.getInvoiceNumber() + "</ar:CbteDesde>" +
                "<ar:CbteHasta>" + request.getInvoiceNumber() + "</ar:CbteHasta>" +
                "<ar:CbteFch>" + request.getInvoiceDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "</ar:CbteFch>" +
                "<ar:ImpTotal>" + request.getTotalAmount() + "</ar:ImpTotal>" +
                "<ar:ImpTotConc>0.00</ar:ImpTotConc>" +
                "<ar:ImpNeto>" + request.getNetAmount() + "</ar:ImpNeto>" +
                "<ar:ImpOpEx>" + request.getExemptAmount() + "</ar:ImpOpEx>" +
                "<ar:ImpTrib>0.00</ar:ImpTrib>" +
                "<ar:ImpIVA>" + request.getTaxAmount() + "</ar:ImpIVA>" +
                "<ar:MonId>" + request.getCurrency() + "</ar:MonId>" +
                "<ar:MonCotiz>" + request.getExchangeRate() + "</ar:MonCotiz>" +
                // Agregar IVAs, tributos, etc.
                "</ar:FECAEDetRequest>" +
                "</ar:FeDetReq>" +
                "</ar:FeCAEReq>" +
                "</ar:FECAESolicitar>" +
                "</soap:Body>" +
                "</soap:Envelope>";
         */
        // @formatter:on
        return null;
    }

    private String sendSOAPRequest(String soapRequest) {
        // Implementar cliente HTTP para SOAP
        // Configurar headers Content-Type, SOAPAction, etc.
        return "soap_response_placeholder";
    }

    private AfipInvoiceResponse parseFECAEResponse(String soapResponse) {
        // TODO: Parsear XML response de WSFEv1. Extraer CAE, fecha vencimiento, errores, etc.
        return new AfipInvoiceResponse("cae_placeholder", LocalDate.now().plusDays(10), "A");
    }
}
