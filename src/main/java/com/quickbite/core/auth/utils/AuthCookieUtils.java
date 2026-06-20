package com.quickbite.core.auth.utils;

import com.quickbite.core.common.config.AppConfig;
import com.quickbite.core.common.enums.AppEnvironment;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieUtils {

    private final AppConfig appConfig;

    // Inject your application configuration
    public AuthCookieUtils(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    /**
     * Helper to build a standard HttpOnly secure cookie for the application.
     */
    private ResponseCookie createHttpOnlyCookie(String name, String value, long maxAgeSeconds, String path) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(appConfig.environment() == AppEnvironment.PRODUCTION) // protect from XSS
                .maxAge(maxAgeSeconds)
                .sameSite("Lax")    // protect from CSRF
                .path(path)
                .build();
    }

    public ResponseCookie createAccessTokenCookie(String token) {
        long maxAgeSeconds = appConfig.jwt().accessExpiresInMs() / 1000;
        return createHttpOnlyCookie(appConfig.cookies().accessTokenName(), token, maxAgeSeconds, "/");
    }

    public ResponseCookie createRefreshTokenCookie(String token) {
        long maxAgeSeconds = appConfig.jwt().refreshExpiresInMs() / 1000;
        return createHttpOnlyCookie(appConfig.cookies().refreshTokenName(), token, maxAgeSeconds,
                "/api/auth/refresh");
    }
}