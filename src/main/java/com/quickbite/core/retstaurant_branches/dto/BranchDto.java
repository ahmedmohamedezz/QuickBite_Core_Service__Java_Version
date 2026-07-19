package com.quickbite.core.retstaurant_branches.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.common.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BranchDto(
        Long id,
        Long restaurantId,
        String countryCode,
        String addressText,
        String label,
        BigDecimal lat,
        BigDecimal lng,
        Boolean isActive,
        LocalTime opensAt,
        LocalTime closesAt,
        Boolean acceptOrders,
        Short deliveryRadius,
        Currency currency,
        Integer commission,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
