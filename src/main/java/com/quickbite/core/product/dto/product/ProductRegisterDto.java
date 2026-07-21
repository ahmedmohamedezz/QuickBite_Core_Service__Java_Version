package com.quickbite.core.product.dto.product;

import com.quickbite.core.product.domain.ProductCategoryEntity;
import com.quickbite.core.restaurant.domain.RestaurantEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.NonNull;

public record ProductRegisterDto(
        @Size(min = 2)
        @NotBlank
        String name,

        String description,

        String imageUrl,

        @NonNull
        RestaurantEntity restaurant,

        ProductCategoryEntity category
) {
}
