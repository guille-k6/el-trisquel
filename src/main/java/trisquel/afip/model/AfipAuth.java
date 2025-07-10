package trisquel.afip.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "afip_wsaa")
public class AfipAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "afip_wsaa_seq")
    @SequenceGenerator(name = "afip_wsaa_seq", sequenceName = "afip_wsaa_seq", allocationSize = 1)
    private Long id;
    private Long uniqueAfipId;
    private OffsetDateTime generationTime;
    private OffsetDateTime expirationTime;
    private String token;
    private String sign;
    private String errorMessage;

    public AfipAuth() {
    }

    public AfipAuth(Long uniqueId, OffsetDateTime generationTime, OffsetDateTime expirationTime, String token,
                    String sign, String errorMessage) {
        this.uniqueAfipId = uniqueId;
        this.generationTime = generationTime;
        this.expirationTime = expirationTime;
        this.token = token;
        this.sign = sign;
        this.errorMessage = errorMessage;
    }

    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(expirationTime.minusMinutes(5)); // 5 min buffer
    }

    public Long getUniqueAfipId() {
        return uniqueAfipId;
    }

    public void setUniqueAfipId(Long uniqueAfipId) {
        this.uniqueAfipId = uniqueAfipId;
    }

    public OffsetDateTime getGenerationTime() {
        return generationTime;
    }

    public void setGenerationTime(OffsetDateTime generationTime) {
        this.generationTime = generationTime;
    }

    public OffsetDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(OffsetDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "AfipAuth {" + "id=" + id + ", uniqueAfipId=" + uniqueAfipId + ", generationTime=" + generationTime + ", expirationTime=" + expirationTime + ", token='" + token + '\'' + ", sign='" + sign + '\'' + ", errorMessage='" + errorMessage + '\'' + '}';
    }
}
