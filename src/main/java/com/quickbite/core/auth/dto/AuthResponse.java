package com.quickbite.core.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private String message;
    private String accessToken;
    private String refreshToken;
    private UserResponse user;
}