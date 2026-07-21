package com.quickbite.core.product.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProductResponse(
        String message,
        ProductDto product
) {
}
