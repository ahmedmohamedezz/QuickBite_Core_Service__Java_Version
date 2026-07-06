package com.quickbite.core.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RestaurantResponse(
        String message,
        RestaurantDto restaurant
) {
}
