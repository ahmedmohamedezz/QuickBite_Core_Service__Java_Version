package com.quickbite.core.auth.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

@Component
@RequiredArgsConstructor
public class AuthUtils {
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
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

    public boolean validateOTP(String plainOTP, String hashedOTP) {
        String hashed = hashOTP(plainOTP);
        return hashedOTP.equals(hashed);
    }
}
