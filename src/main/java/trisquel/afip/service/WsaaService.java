package trisquel.afip.service;

import jakarta.annotation.PostConstruct;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import trisquel.afip.config.AfipURLs;
import trisquel.afip.model.AfipAuth;
import trisquel.afip.repository.AfipAuthRepository;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WsaaService {

    private final Resource certResource;
    private final Resource keyResource;

    @Value("${afip.service}")
    private String service;

    private final AfipAuthRepository afipAuthRepository;


    @PostConstruct
    public void init() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    WsaaService(AfipAuthRepository afipAuthRepository, @Value("${afip.cert.path}") Resource certResource,
                @Value("${afip.key.path}") Resource keyResource) {
        this.afipAuthRepository = afipAuthRepository;
        this.certResource = certResource;
        this.keyResource = keyResource;
    }

    public AfipAuth autenticar() throws Exception {
        Optional<AfipAuth> activeAuth = afipAuthRepository.findLastAuth(OffsetDateTime.now().plusMinutes(5));
        if (activeAuth.isPresent() && activeAuth.get().isSuccessFul()) {
            return activeAuth.get();
        }
        String traXml = crearTRA();
        byte[] cms = firmarTRA(traXml);
        String response = enviarCMSaWSAA(cms);
        AfipAuth afipAuth = parseAuthenticationFromResponse(response);
        return afipAuthRepository.save(afipAuth);
        // return extraerCredenciales(response);
    }

    private String crearTRA() {
        ZonedDateTime ahora = ZonedDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

        String uniqueId = String.valueOf(System.currentTimeMillis() / 1000);

        return """
                <loginTicketRequest version="1.0">
                   <header>
                      <uniqueId>%s</uniqueId>
                      <generationTime>%s</generationTime>
                      <expirationTime>%s</expirationTime>
                   </header>
                   <service>%s</service>
                </loginTicketRequest>
                """.formatted(uniqueId, fmt.format(ahora.minusMinutes(5)), fmt.format(ahora.plusMinutes(5)), service);
    }

    private byte[] firmarTRA(String traXml) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) cf.generateCertificate(certResource.getInputStream());
        PrivateKey privateKey = cargarClavePrivada();

        CMSProcessableByteArray content = new CMSProcessableByteArray(traXml.getBytes(StandardCharsets.UTF_8));
        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(privateKey);
        generator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(signer, cert));
        generator.addCertificate(new JcaX509CertificateHolder(cert));

        CMSSignedData signedData = generator.generate(content, true);
        return signedData.getEncoded();
    }

    private PrivateKey cargarClavePrivada() throws Exception {
        try (Reader reader = new InputStreamReader(keyResource.getInputStream(), StandardCharsets.UTF_8); PEMParser pemParser = new PEMParser(reader)) {

            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

            if (object instanceof PEMKeyPair pair) {
                return converter.getKeyPair(pair).getPrivate();
            } else if (object instanceof PrivateKeyInfo info) {
                return converter.getPrivateKey(info);
            } else {
                throw new IllegalArgumentException("Formato de clave no reconocido");
            }
        }
    }

    private String enviarCMSaWSAA(byte[] cmsFirmado) throws Exception {
        String base64 = Base64.getEncoder().encodeToString(cmsFirmado);

        String soap = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                  xmlns:ws="http://wsaa.view.sua.dvadac.desein.afip.gov">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <ws:loginCms>
                         <ws:in0><![CDATA[%s]]></ws:in0>
                      </ws:loginCms>
                   </soapenv:Body>
                </soapenv:Envelope>
                """.formatted(base64);

        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder().uri(URI.create(AfipURLs.WsaaURL)).header("Content-Type", "text/xml; charset=utf-8").header("SOAPAction", "").POST(java.net.http.HttpRequest.BodyPublishers.ofString(soap)).build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private AfipAuth parseAuthenticationFromResponse(String responseBody) {
        Long uniqueId = null;
        OffsetDateTime generationTime = OffsetDateTime.now();
        OffsetDateTime expirationTime = null;
        String token = null;
        String sign = null;

        String parsedResponse = responseBody.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&amp;", "&");
        Pattern loginCmsReturnPattern = Pattern.compile("<loginTicketResponse(.*?)</loginTicketResponse>", Pattern.DOTALL);
        Matcher returnMatcher = loginCmsReturnPattern.matcher(parsedResponse);
        if (!returnMatcher.find()) {
            String errorMessage;
            Pattern errorPattern = Pattern.compile("<faultstring(.*?)</faultstring>", Pattern.DOTALL);
            Matcher errorMatcher = errorPattern.matcher(parsedResponse);
            if (!errorMatcher.find()) {
                errorMessage = parsedResponse;
            }
            errorMessage = errorMatcher.group(1);
            AfipAuth auth = new AfipAuth(uniqueId, generationTime, expirationTime, token, sign, errorMessage);
            return auth;
        }

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
        Pattern signPattern = Pattern.compile("<sign>(.*?)</sign>");
        Matcher signMatcher = signPattern.matcher(parsedResponse);
        if (signMatcher.find()) {
            sign = signMatcher.group(1);
        }
        return new AfipAuth(uniqueId, generationTime, expirationTime, token, sign, null);
    }

    private String extractSoapFault(String soapResponse) {
        Pattern pattern = Pattern.compile("<faultstring>(.*?)</faultstring>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(soapResponse);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Error desconocido";
    }
}
