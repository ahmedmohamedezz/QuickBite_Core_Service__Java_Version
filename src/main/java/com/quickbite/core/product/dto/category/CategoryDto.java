package com.quickbite.core.product.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.product.dto.product.ProductDto;
import com.quickbite.core.restaurant.dto.RestaurantDto;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryDto(
        Long id,
        RestaurantDto restaurant,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ProductDto> products
) {
}
