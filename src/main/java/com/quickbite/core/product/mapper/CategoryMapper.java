package com.quickbite.core.product.mapper;

import com.quickbite.core.product.domain.CategoryEntity;
import com.quickbite.core.product.dto.category.CategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CategoryMapper {
    @Mapping(target = "restaurantId", source = "restaurant.id")
    CategoryDto toDto(CategoryEntity entity);

    @Mapping(target = "id", ignore = true)
    CategoryEntity toEntity(CategoryDto categoryDto);
}
