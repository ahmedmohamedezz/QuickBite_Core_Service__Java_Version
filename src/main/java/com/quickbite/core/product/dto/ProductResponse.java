package com.quickbite.core.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.user.dto.UserDto;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProductResponse(
        String message,
        ProductDto product
) {
}
