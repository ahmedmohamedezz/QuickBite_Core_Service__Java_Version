package com.quickbite.core.common.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public record AppConfig(
        int port,
        DbConfig db,
        JwtConfig jwt
) {
    public record DbConfig(
            String host,
            int port,
            String username,
            @NotBlank String password,
            @NotBlank String name,
            int poolMin,
            int poolMax,
            @NotBlank String migrationDirectory,
            @NotBlank String migrationExtension
    ) {
    }

    public record JwtConfig(
            @NotBlank String refreshSecret,
            @NotBlank String accessSecret,
            @NotBlank String accessExpiresIn,
            @NotBlank String refreshExpiresIn
    ) {
    }
}
