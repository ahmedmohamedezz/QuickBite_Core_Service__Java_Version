package com.quickbite.core.retstaurant_branches.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.common.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BranchUpdateDto(
        String addressText,
        String label,
        BigDecimal lat,
        BigDecimal lng,
        LocalTime opensAt,
        LocalTime closesAt,
        Boolean acceptOrders,
        Short deliveryRadius,
        Currency currency
) {
}
