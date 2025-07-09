package trisquel.afip.auth;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import trisquel.afip.config.AfipConfiguration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WSAAJavaClient {

    private RestTemplate restTemplate;
    private final AfipConfiguration afipConfiguration;
    private final ResourceLoader resourceLoader;
    private static final Logger logger = Logger.getLogger(WSAAJavaClient.class.getName());
    private final String certificatePath;
    private final String privateKeyPath;
    private final String serviceId = "wsfe";
    private final String wsaaUrl = "https://wsaahomo.afip.gov.ar/ws/services/LoginCms";

    public WSAAJavaClient(RestTemplate restTemplate, AfipConfiguration afipConfiguration,
                          ResourceLoader resourceLoader) {
        this.restTemplate = restTemplate;
        this.afipConfiguration = afipConfiguration;
        this.resourceLoader = resourceLoader;
        this.certificatePath = getActualFilePath(afipConfiguration.getCertificatePath());
        this.privateKeyPath = getActualFilePath(afipConfiguration.getPrivateKeyPath());
    }

    /**
     * Actually authenticates over the WSAA of AFIP
     *
     * @return response of the authentication
     * @throws Exception
     */
    public String authenticate() throws Exception {
        SignatureUtils signatureUtils = new SignatureUtils(certificatePath, privateKeyPath);
        String xmlContent = createLoginTicketRequestXML();
        byte[] cmsData = signatureUtils.signCMS(xmlContent);
        String base64CMS = Base64.getEncoder().encodeToString(cmsData);
        String response = invokeWSAAWithRestTemplate(base64CMS);
        return response;
    }

    public String createLoginTicketRequestXML() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        // Crear elemento raíz
        Element loginTicketRequest = doc.createElement("loginTicketRequest");
        doc.appendChild(loginTicketRequest);
        Element header = doc.createElement("header");
        loginTicketRequest.appendChild(header);
        // uniqueId (equivalente a $dtNow.ToString("yyMMddHHMM"))
        Element uniqueId = doc.createElement("uniqueId");
        uniqueId.appendChild(doc.createTextNode(now.format(DateTimeFormatter.ofPattern("yyMMddHHmm"))));
        header.appendChild(uniqueId);
        // generationTime (equivalente a $dtNow.AddMinutes(-10).ToString("s"))
        Element generationTime = doc.createElement("generationTime");
        generationTime.appendChild(doc.createTextNode(now.minusMinutes(10).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
        header.appendChild(generationTime);
        // expirationTime (equivalente a $dtNow.AddMinutes(+10).ToString("s"))
        Element expirationTime = doc.createElement("expirationTime");
        expirationTime.appendChild(doc.createTextNode(now.plusMinutes(10).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
        header.appendChild(expirationTime);
        // service
        Element service = doc.createElement("service");
        service.appendChild(doc.createTextNode(serviceId));
        loginTicketRequest.appendChild(service);
        // Convertir a String XML
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        String xmlContent = writer.getBuffer().toString();

        return xmlContent;
    }

    public String invokeWSAAWithRestTemplate(String base64CMS) throws Exception {
        String soapRequest = buildSoapRequest(base64CMS);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("SOAPAction", "");
        headers.set("User-Agent", "Java-WSAA-Client/1.0");

        HttpEntity<String> requestEntity = new HttpEntity<>(soapRequest, headers);
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(wsaaUrl, HttpMethod.POST, requestEntity, String.class);

            String responseBody = response.getBody();

            return extractLoginTicketResponse(responseBody);

        } catch (Exception e) {
            // Authentication failed. What happened?
            String fault = response.getBody();
            logger.severe("Error en WSAA: " + e.getMessage());
            throw e;
        }
    }

    private String extractSoapFault(String soapResponse) {
        Pattern pattern = Pattern.compile("<faultstring>(.*?)</faultstring>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(soapResponse);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Error desconocido";
    }


    private String buildSoapRequest(String base64CMS) {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                    <soap:Header/>
                    <soap:Body>
                        <loginCms xmlns="https://wsaa.afip.gov.ar/ws/services/LoginCms">
                            <request>%s</request>
                        </loginCms>
                    </soap:Body>
                </soap:Envelope>
                """.formatted(base64CMS);
    }

    private String extractLoginTicketResponse(String soapResponse) {
        Pattern pattern = Pattern.compile("<loginCmsReturn>(.*?)</loginCmsReturn>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(soapResponse);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        pattern = Pattern.compile("<.*?:loginCmsReturn>(.*?)</.*?:loginCmsReturn>", Pattern.DOTALL);
        matcher = pattern.matcher(soapResponse);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        logger.warning("No se pudo extraer loginCmsReturn, devolviendo respuesta completa");
        return soapResponse;
    }

    private String getActualFilePath(String resourcePath) {
        try {
            if (resourcePath.startsWith("classpath:")) {
                Resource resource = resourceLoader.getResource(resourcePath);
                return resource.getFile().getAbsolutePath();
            }
            return resourcePath;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
