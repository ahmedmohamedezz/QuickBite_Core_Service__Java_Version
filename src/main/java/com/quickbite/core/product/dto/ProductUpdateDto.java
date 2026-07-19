package com.quickbite.core.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.product.domain.ProductCategoryEntity;
import com.quickbite.core.restaurant.domain.RestaurantEntity;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductUpdateDto(
        String name,
        String description,
        String imageUrl,
        RestaurantEntity restaurant,
        ProductCategoryEntity category
) {
}
