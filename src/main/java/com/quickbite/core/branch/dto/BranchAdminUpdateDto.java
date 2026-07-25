package com.quickbite.core.branch.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.common.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BranchAdminUpdateDto(
        Boolean isActive,
        Integer commission
) {
}
