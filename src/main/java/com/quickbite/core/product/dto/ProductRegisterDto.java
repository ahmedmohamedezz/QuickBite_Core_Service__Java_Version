package com.quickbite.core.product.dto;

import com.quickbite.core.product.domain.ProductCategoryEntity;
import com.quickbite.core.restaurant.domain.RestaurantEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.NonNull;

import java.time.LocalDateTime;

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
