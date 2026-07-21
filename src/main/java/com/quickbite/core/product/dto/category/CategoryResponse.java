package com.quickbite.core.product.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record CategoryResponse(
        String message,
        CategoryDto product
) {
}
