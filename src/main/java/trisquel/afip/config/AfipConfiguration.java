package trisquel.afip.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "afip")
public class AfipConfiguration {
    private String wsaaUrl = "https://wsaahomo.afip.gov.ar/ws/services/LoginCms"; // Homologación
    private String wsfev1Url = "https://wswhomo.afip.gov.ar/wsfev1/service.asmx"; // Homologación
    private String certificatePath;
    private String certificatePassword;
    private String privateKeyPath;
    private Long cuit;
    private String service = "wsfe";
    private int tokenExpirationHours = 12;
    private int maxRetries = 3;

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getTokenExpirationHours() {
        return tokenExpirationHours;
    }

    public void setTokenExpirationHours(int tokenExpirationHours) {
        this.tokenExpirationHours = tokenExpirationHours;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Long getCuit() {
        return cuit;
    }

    public void setCuit(Long cuit) {
        this.cuit = cuit;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public String getCertificatePassword() {
        return certificatePassword;
    }

    public void setCertificatePassword(String certificatePassword) {
        this.certificatePassword = certificatePassword;
    }

    public String getCertificatePath() {
        return certificatePath;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

    public String getWsfev1Url() {
        return wsfev1Url;
    }

    public void setWsfev1Url(String wsfev1Url) {
        this.wsfev1Url = wsfev1Url;
    }

    public String getWsaaUrl() {
        return wsaaUrl;
    }

    public void setWsaaUrl(String wsaaUrl) {
        this.wsaaUrl = wsaaUrl;
    }
}
