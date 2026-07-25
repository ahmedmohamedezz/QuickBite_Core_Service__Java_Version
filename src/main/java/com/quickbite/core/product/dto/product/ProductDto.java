package com.quickbite.core.product.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.product.dto.category.CategoryDto;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductDto(
        Long id,
        String name,
        String description,
        String imageUrl,
        Long restaurantId,
        Long categoryId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
