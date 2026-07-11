package com.quickbite.core.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.restaurant.dto.RestaurantDto;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        String message,
        UserDto user,
        RestaurantDto restaurant
) {
}