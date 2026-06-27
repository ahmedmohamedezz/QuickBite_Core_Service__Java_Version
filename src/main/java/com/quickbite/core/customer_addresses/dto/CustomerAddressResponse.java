package com.quickbite.core.customer_addresses.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;


@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomerAddressResponse(
        String message,
        CustomerAddressDto address
) {
}
