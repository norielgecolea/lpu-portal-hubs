package org.nors.dev.codes.lpu.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret;
    private String secretFile = "data/secrets/jwt-secret";
    private long expirationMs = 28_800_000L;
    private long rememberExpirationMs = 315_360_000_000L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSecretFile() {
        return secretFile;
    }

    public void setSecretFile(String secretFile) {
        this.secretFile = secretFile;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }

    public long getRememberExpirationMs() {
        return rememberExpirationMs;
    }

    public void setRememberExpirationMs(long rememberExpirationMs) {
        this.rememberExpirationMs = rememberExpirationMs;
    }
}
