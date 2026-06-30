package com.quickbite.core.customer_addresses.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.customer_addresses.enums.AddressType;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Builder
@Jacksonized        // to fix request de-serialization if sent in API
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomerAddressDto(
        Long id,
        String label,
        String country,
        String city,
        String street,
        String building,
        String apartmentNumber,
        AddressType type,
        BigDecimal lat,
        BigDecimal lng,
        Boolean isDefault
) {
}
