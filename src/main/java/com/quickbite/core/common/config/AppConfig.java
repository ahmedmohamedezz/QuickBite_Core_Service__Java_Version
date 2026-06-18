package com.quickbite.core.common.config;

import com.quickbite.core.common.enums.AppEnvironment;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppConfig(
        int serverPort,
        AppEnvironment environment,
        DbConfig db,
        JwtConfig jwt,
        PasswordEncoder passwordEncoder
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
            @NotBlank long accessExpiresInMs,
            @NotBlank long refreshExpiresInMs
    ) {
    }

    public record PasswordEncoder(
            @Min(4) @Max(31) int salt
    ) {}
}
