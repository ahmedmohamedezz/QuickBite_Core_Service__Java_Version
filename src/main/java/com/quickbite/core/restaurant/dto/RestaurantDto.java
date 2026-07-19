package com.quickbite.core.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.restaurant.enums.RestaurantStatus;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RestaurantDto(
        Long id,
        String name,
        String logoURL,
        String primaryCountry,
        Long ownerId,
        RestaurantStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime statusUpdatedAt
) {
}
