package com.quickbite.core.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

@Configuration
public class EncryptionConfig {
    private final AppConfig appConfig;

    public EncryptionConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(appConfig.passwordEncoder().salt());
    }

    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }
}