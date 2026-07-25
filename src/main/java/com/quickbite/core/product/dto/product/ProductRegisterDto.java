package com.quickbite.core.product.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductRegisterDto(
        @Size(min = 2)
        @NotBlank
        String name,

        String description,

        String imageUrl,

        String categoryName
) {
    // compact constructor
    public ProductRegisterDto {
        if (categoryName != null) {
            categoryName = categoryName.trim().toLowerCase();
        }
    }
}
