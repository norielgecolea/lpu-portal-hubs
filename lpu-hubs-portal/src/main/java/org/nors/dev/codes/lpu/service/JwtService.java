package org.nors.dev.codes.lpu.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Date;
import javax.crypto.SecretKey;
import org.nors.dev.codes.lpu.config.JwtProperties;
import org.nors.dev.codes.lpu.model.User;
import org.nors.dev.codes.lpu.security.JwtSecretResolver;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        String secretMaterial = new JwtSecretResolver().resolve(
                jwtProperties.getSecret(),
                Path.of(jwtProperties.getSecretFile())
        );
        this.secretKey = buildKey(secretMaterial);
    }

    private static SecretKey buildKey(String secret) {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (Exception ex) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            keyBytes = padded;
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user, boolean rememberMe) {
        long now = System.currentTimeMillis();
        long expirationMs = rememberMe
                ? jwtProperties.getRememberExpirationMs()
                : jwtProperties.getExpirationMs();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .claim("uid", user.getId())
                .claim("sv", user.getSessionVersion())
                .claim("rm", rememberMe)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    public Long extractUserId(String token) {
        Object uid = parseClaims(token).get("uid");
        if (uid instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    public int extractSessionVersion(String token) {
        Object version = parseClaims(token).get("sv");
        if (version instanceof Number number) {
            return number.intValue();
        }
        return 0;
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public long getExpirationMs(boolean rememberMe) {
        return rememberMe
                ? jwtProperties.getRememberExpirationMs()
                : jwtProperties.getExpirationMs();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
