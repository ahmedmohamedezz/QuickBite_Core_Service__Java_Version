package com.quickbite.core.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.restaurant.enums.RestaurantStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RestaurantAdminUpdateDto(
        RestaurantStatus status
) {
}
