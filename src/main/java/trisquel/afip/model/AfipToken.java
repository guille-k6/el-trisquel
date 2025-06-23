package trisquel.afip.model;

import java.time.LocalDateTime;

public class AfipToken {
    private String token;
    private String sign;
    private LocalDateTime expiresAt;

    public AfipToken(String token, String sign, LocalDateTime expiresAt) {
        this.token = token;
        this.sign = sign;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt.minusMinutes(5)); // 5 min buffer
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

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
