package com.quickbite.core.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.restaurant.dto.RestaurantDto;
import com.quickbite.core.user.dto.UserDto;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(
        String message,
        String accessToken,
        String refreshToken,
        UserDto user,
        RestaurantDto restaurant
) {
    // secondary constructor without restaurant info
    public AuthResponse(String message,
                        String accessToken,
                        String refreshToken,
                        UserDto user) {
        this(message, accessToken, refreshToken, user, null);
    }
}