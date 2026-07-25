package com.quickbite.core.product.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductBranchDetailsDto(
        Integer price,
        Integer stock,
        Boolean isAvailable
) {
}
