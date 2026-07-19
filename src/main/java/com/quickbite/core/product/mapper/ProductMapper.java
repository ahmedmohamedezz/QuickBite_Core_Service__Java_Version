package com.quickbite.core.product.mapper;

import com.quickbite.core.product.domain.ProductEntity;
import com.quickbite.core.product.dto.ProductDto;
import com.quickbite.core.product.dto.ProductUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductMapper {
    ProductDto toDto(ProductEntity entity);

    @Mapping(target = "id", ignore = true)
    ProductEntity toEntity(ProductDto productDto);

    void updateEntityFromDto(ProductUpdateDto productDto,
                             @MappingTarget ProductEntity productEntity);
}
