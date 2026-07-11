package com.quickbite.core.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.user.dto.UserDto;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record RestaurantResponse(
        String message,
        RestaurantDto restaurant,
        UserDto owner
) {
}
