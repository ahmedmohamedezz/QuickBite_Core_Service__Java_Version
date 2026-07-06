package com.quickbite.core.restaurant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RestaurantRegisterDto(
        @Min(2)
        @NotBlank
        String name,

        String logoURL,

        @Min(2)
        @NotBlank
        String primaryCountry
) {
}
