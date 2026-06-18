package com.quickbite.core.auth.utils;

import com.quickbite.core.auth.dto.JwtPayload;
import com.quickbite.core.common.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

@Component
public class AuthUtils {
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SecureRandom secureRandom;

    public AuthUtils(PasswordEncoder passwordEncoder, JwtService jwtService, SecureRandom secureRandom) {
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.secureRandom = secureRandom;
    }

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public String generateAccessToken(Long id, String email, String systemRole) {
        return jwtService.generateAccessToken(id, email, systemRole);
    }

    public String generateRefreshToken(Long id, String email, String systemRole) {
        return jwtService.generateRefreshToken(id, email, systemRole);
    }

    public JwtPayload verifyAccessToken(String accessToken) {
        Claims claims = jwtService.validateAndExtractAccessClaims(accessToken);
        return parseClaims(claims);
    }

    public JwtPayload verifyRefreshToken(String refreshToken) {
        Claims claims = jwtService.validateAndExtractRefreshClaims(refreshToken);
        return parseClaims(claims);
    }

    private JwtPayload parseClaims(Claims claims) {
        return new JwtPayload(
                Long.parseLong(claims.getSubject()),
                claims.get("email", String.class),
                claims.get("role", String.class)
        );
    }

    public boolean comparePassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }

    public String generateOTP() {
        // generate a 6-digit otp
        int otp = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(otp);
    }

    public String hashOTP(String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
