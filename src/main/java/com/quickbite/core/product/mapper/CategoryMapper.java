package com.quickbite.core.product.mapper;

import com.quickbite.core.product.domain.ProductCategoryEntity;
import com.quickbite.core.product.domain.ProductEntity;
import com.quickbite.core.product.dto.category.CategoryDto;
import com.quickbite.core.product.dto.product.ProductDto;
import com.quickbite.core.product.dto.product.ProductUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CategoryMapper {
    CategoryDto toDto(ProductCategoryEntity entity);

    @Mapping(target = "id", ignore = true)
    ProductCategoryEntity toEntity(CategoryDto categoryDto);
}
