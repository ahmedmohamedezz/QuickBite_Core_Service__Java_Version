package com.quickbite.core.auth.controller;

import com.quickbite.core.auth.dto.*;
import com.quickbite.core.auth.service.AuthService;
import com.quickbite.core.auth.utils.AuthCookieUtils;
import com.quickbite.core.common.dto.ApiResponse;
import com.quickbite.core.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthCookieUtils authCookieUtils;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, AuthCookieUtils authCookieUtils) {
        this.authService = authService;
        this.authCookieUtils = authCookieUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterDto request) {
        AuthResponse data = authService.register(request);
        UserResponse response = data.getUser();
        response.setMessage(data.getMessage());

        ResponseCookie accessTokenCookie = authCookieUtils.createAccessTokenCookie(data.getAccessToken());
        ResponseCookie refreshTokenCookie = authCookieUtils.createRefreshTokenCookie(data.getRefreshToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody UserLoginDto request) {
        AuthResponse data = authService.login(request);
        UserResponse response = data.getUser();
        response.setMessage(data.getMessage());

        ResponseCookie accessTokenCookie = authCookieUtils.createAccessTokenCookie(data.getAccessToken());
        ResponseCookie refreshTokenCookie = authCookieUtils.createRefreshTokenCookie(data.getRefreshToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    @PostMapping("/forget-password")
    public ResponseEntity<ApiResponse> forgetPassword(@Valid @RequestBody ForgetPasswordDto request) {
        authService.forgetPassword(request);
        return ResponseEntity.ok(new ApiResponse("Email Sent with OTP"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordDto request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(new ApiResponse("Password reset successfully, please login again"));
    }

    @GetMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshToken(@CookieValue(name = "refreshToken") String refreshToken) {
        String newAccessToken = authService.refreshToken(refreshToken);

        ResponseCookie accessTokenCookie = authCookieUtils.createAccessTokenCookie(newAccessToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .body(new ApiResponse("Success"));
    }
}