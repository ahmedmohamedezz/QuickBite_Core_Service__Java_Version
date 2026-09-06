package com.quickbite.core.rbac.mapper;

import com.quickbite.core.product.domain.ProductEntity;
import com.quickbite.core.product.dto.product.ProductDto;
import com.quickbite.core.product.dto.product.ProductUpdateDto;
import com.quickbite.core.rbac.domain.RestaurantMemberEntity;
import com.quickbite.core.rbac.dto.RestaurantMemberDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RestaurantMemberMapper {
    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "roleId", source = "role.id")
    RestaurantMemberDto toDto(RestaurantMemberEntity entity);

//    @Mapping(target = "id", ignore = true)
//    RestaurantMemberEntity toEntity(RestaurantMemberDto productDto);
}
