package com.quickbite.core.common.security;

import com.quickbite.core.common.config.AppConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtService(AppConfig appConfig) {
        AppConfig.JwtConfig jwtConfig = appConfig.jwt();

        this.accessKey = Keys.hmacShaKeyFor(jwtConfig.accessSecret().getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(jwtConfig.refreshSecret().getBytes(StandardCharsets.UTF_8));

        this.accessExpirationMs = jwtConfig.accessExpiresIn();
        this.refreshExpirationMs = jwtConfig.refreshExpiresIn();
    }

    public String generateAccessToken(Long userId, String email, String role) {
        return buildToken(userId, email, role, accessExpirationMs, accessKey);
    }

    public String generateRefreshToken(Long userId, String email, String role) {
        return buildToken(userId, email, role, refreshExpirationMs, refreshKey);
    }

    public Claims validateAndExtractAccessClaims(String token) {
        return validateAndExtract(token, accessKey);
    }

    public Claims validateAndExtractRefreshClaims(String token) {
        return validateAndExtract(token, refreshKey);
    }

    private String buildToken(Long userId, String email, String role, long expirationMs, SecretKey key) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claims(Map.of("email", email, "role", role))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    private Claims validateAndExtract(String token, SecretKey key) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null; // Token is invalid, expired, or tampered with
        }
    }
}