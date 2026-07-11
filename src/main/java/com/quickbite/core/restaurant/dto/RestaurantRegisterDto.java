package com.quickbite.core.restaurant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestaurantRegisterDto(
        @Size(min = 2)
        @NotBlank
        String name,

        @NotBlank
        String logoURL,

        @Size(min = 2)
        @NotBlank
        String primaryCountry
) {
}
