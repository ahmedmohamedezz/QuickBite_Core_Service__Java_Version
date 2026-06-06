package com.quickbite.core.auth.dto;

import com.quickbite.core.user.dto.UserResponse;
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