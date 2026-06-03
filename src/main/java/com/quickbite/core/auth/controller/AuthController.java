package com.quickbite.core.auth.controller;

import com.quickbite.core.auth.dto.*;
import com.quickbite.core.auth.service.AuthService;
import com.quickbite.core.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegisterDto request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginDto request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
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
}