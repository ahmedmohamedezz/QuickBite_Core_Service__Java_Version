package com.quickbite.core.product.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductUpdateDto(
        String name,
        String description,
        String imageUrl,

        String categoryName,

        // branch-level
        Integer price,
        Integer stock,
        Boolean isAvailable
) {
    public ProductUpdateDto {
        if (categoryName != null) {
            categoryName = categoryName.trim().toLowerCase();
        }
    }
}
