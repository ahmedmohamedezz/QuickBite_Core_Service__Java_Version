package com.quickbite.core.product.mapper;

import com.quickbite.core.product.domain.ProductBranchDetailsEntity;
import com.quickbite.core.product.domain.ProductEntity;
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
public interface ProductBranchMapper {
    void updateEntityFromDto(ProductUpdateDto productDto,
                             @MappingTarget ProductBranchDetailsEntity productBranchDetailsEntity);
}
