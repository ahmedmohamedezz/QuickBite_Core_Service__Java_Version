package com.quickbite.core.retstaurant_branches.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.common.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BranchRegisterDto(
        @NotBlank(message = "countryCode is required")
        String countryCode,

        @NotBlank(message = "addressText is required")
        String addressText,

        @NotBlank(message = "label is required")
        String label,

        @NotNull(message = "lat (latitude) is required")
        BigDecimal lat,

        @NotNull(message = "lng (longitude) is required")
        BigDecimal lng,

        @NotNull(message = "opensAt is required")
        LocalTime opensAt,

        @NotNull(message = "closesAt is required")
        LocalTime closesAt,

        @NotNull(message = "deliveryRadius is required")
        Short deliveryRadius,

        Currency currency
) {
}
