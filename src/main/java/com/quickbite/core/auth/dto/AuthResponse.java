package com.quickbite.core.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.user.dto.UserDto;
import com.quickbite.core.user.dto.UserResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private String message;
    private String accessToken;
    private String refreshToken;
    private UserDto user;
}