package com.quickbite.core.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.auth.dto.RestaurantOwnerDto;
import com.quickbite.core.restaurant.enums.RestaurantStatus;
import com.quickbite.core.user.dto.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RestaurantWithOwnerDto(
        @Size(min = 2)
        @NotBlank
        String name,

        @NotBlank
        String logoURL,

        @Size(min = 2)
        @NotBlank
        String primaryCountry,

        @NotNull
        @Valid
        RestaurantOwnerDto owner
) {
}
