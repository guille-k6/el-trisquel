package trisquel.afip.auth;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import trisquel.afip.config.AfipConfiguration;
import trisquel.afip.model.AfipAuth;
import trisquel.afip.repository.AfipAuthRepository;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AfipAuthService {

    private final AfipAuthRepository afipAuthRepository;
    private RestTemplate restTemplate;
    private final AfipConfiguration afipConfiguration;
    private final ResourceLoader resourceLoader;
    private static final Logger logger = Logger.getLogger(AfipAuthService.class.getName());
    private final String certificatePath;
    private final String privateKeyPath;
    private final String serviceId = "wsfe";
    private final String wsaaUrl = "https://wsaahomo.afip.gov.ar/ws/services/LoginCms";

    public AfipAuthService(RestTemplate restTemplate, AfipConfiguration afipConfiguration,
                           ResourceLoader resourceLoader, AfipAuthRepository afipAuthRepository) {
        this.restTemplate = restTemplate;
        this.afipConfiguration = afipConfiguration;
        this.resourceLoader = resourceLoader;
        this.certificatePath = getActualFilePath(afipConfiguration.getCertificatePath());
        this.privateKeyPath = getActualFilePath(afipConfiguration.getPrivateKeyPath());
        this.afipAuthRepository = afipAuthRepository;
    }

    /**
     * Actually authenticates over the WSAA of AFIP
     *
     * @return response of the authentication
     * @throws Exception
     */
    public AfipAuth authenticate() throws Exception {
        Optional<AfipAuth> activeAuth = afipAuthRepository.findLastAuth(OffsetDateTime.now().plusMinutes(5));
        if (activeAuth.isPresent()) {
            return activeAuth.get();
        }
        SignatureUtils signatureUtils = new SignatureUtils(certificatePath, privateKeyPath);
        String xmlContent = createLoginTicketRequestXML();
        byte[] cmsData = signatureUtils.signCMS(xmlContent);
        String base64CMS = Base64.getEncoder().encodeToString(cmsData);
        AfipAuth auth = invokeWSAAWithRestTemplate(base64CMS);
        return auth;
    }

    public String createLoginTicketRequestXML() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        Element loginTicketRequest = doc.createElement("loginTicketRequest");
        doc.appendChild(loginTicketRequest);
        Element header = doc.createElement("header");
        loginTicketRequest.appendChild(header);
        Element uniqueId = doc.createElement("uniqueId");
        uniqueId.appendChild(doc.createTextNode(now.format(DateTimeFormatter.ofPattern("yyMMddHHmm"))));
        header.appendChild(uniqueId);
        Element generationTime = doc.createElement("generationTime");
        generationTime.appendChild(doc.createTextNode(now.minusMinutes(10).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
        header.appendChild(generationTime);
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

    public AfipAuth invokeWSAAWithRestTemplate(String base64CMS) throws Exception {
        String soapRequest = buildSoapRequest(base64CMS);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("SOAPAction", "");
        headers.set("User-Agent", "Java-WSAA-Client/1.0");

        HttpEntity<String> requestEntity = new HttpEntity<>(soapRequest, headers);
        AfipAuth afipAuth;
        try {
            ResponseEntity<String> response = restTemplate.exchange(wsaaUrl, HttpMethod.POST, requestEntity, String.class);
            String responseBody = response.getBody();
            afipAuth = parseAuthenticationFromResponse(responseBody);
            afipAuth = afipAuthRepository.save(afipAuth);
        } catch (HttpServerErrorException e) {
            // Error del ws de afip. Posibles causas: mala request, pediste un tklen hace muy poco tiempo, etc etc etc.
            afipAuth = parseFailedAuthentication(e.getMessage());
            afipAuth = afipAuthRepository.save(afipAuth);
        } catch (Exception e) {
            logger.severe("Error en WSAA: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return afipAuth;
    }

    private AfipAuth parseAuthenticationFromResponse(String responseBody) {

        String parsedResponse = responseBody.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&amp;", "&");
        Pattern loginCmsReturnPattern = Pattern.compile("<loginTicketResponse(.*?)</loginTicketResponse>", Pattern.DOTALL);
        Matcher returnMatcher = loginCmsReturnPattern.matcher(parsedResponse);
        if (!returnMatcher.find()) {
            throw new RuntimeException("Error en WSAA: " + "No se encontro el bloque <loginTicketResponse>");
        }

        Long uniqueId = null;
        OffsetDateTime generationTime = null;
        OffsetDateTime expirationTime = null;
        String token = null;
        String sign = null;

        Pattern idPattern = Pattern.compile("<uniqueId>(.*?)</uniqueId>");
        Matcher idMatcher = idPattern.matcher(parsedResponse);
        if (idMatcher.find()) {
            uniqueId = Long.valueOf(idMatcher.group(1));
        }
        Pattern genPattern = Pattern.compile("<generationTime>(.*?)</generationTime>");
        Matcher genMatcher = genPattern.matcher(parsedResponse);
        if (genMatcher.find()) {
            generationTime = OffsetDateTime.parse(genMatcher.group(1));
        }
        Pattern expPattern = Pattern.compile("<expirationTime>(.*?)</expirationTime>");
        Matcher expMatcher = expPattern.matcher(parsedResponse);
        if (expMatcher.find()) {
            expirationTime = OffsetDateTime.parse(expMatcher.group(1));
        }
        Pattern tokenPattern = Pattern.compile("<token>(.*?)</token>");
        Matcher tokenMatcher = tokenPattern.matcher(parsedResponse);
        if (tokenMatcher.find()) {
            token = tokenMatcher.group(1);
        }
        Pattern signPattern = Pattern.compile("<token>(.*?)</token>");
        Matcher signMatcher = signPattern.matcher(parsedResponse);
        if (signMatcher.find()) {
            sign = signMatcher.group(1);
        }
        return new AfipAuth(uniqueId, generationTime, expirationTime, token, sign, null);
    }

    private AfipAuth parseFailedAuthentication(String errorBody) {
        String parsedError = errorBody.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&amp;", "&");
        Long uniqueId = null;
        OffsetDateTime generationTime = OffsetDateTime.now();
        OffsetDateTime expirationTime = null;
        String token = null;
        String sign = null;
        String errorMessage;

        Pattern errorPattern = Pattern.compile("<faultstring(.*?)</faultstring>", Pattern.DOTALL);
        Matcher errorMatcher = errorPattern.matcher(parsedError);
        if (!errorMatcher.find()) {
            errorMessage = parsedError;
        }
        errorMessage = errorMatcher.group(1);
        AfipAuth auth = new AfipAuth(uniqueId, generationTime, expirationTime, token, sign, errorMessage);
        return auth;
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
