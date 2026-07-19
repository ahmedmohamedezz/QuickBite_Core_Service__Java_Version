package com.quickbite.core.restaurant.mapper;

import com.quickbite.core.restaurant.domain.RestaurantEntity;
import com.quickbite.core.restaurant.dto.RestaurantDto;
import com.quickbite.core.restaurant.dto.RestaurantUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RestaurantMapper {
    @Mapping(target = "ownerId", source = "owner.id")
    RestaurantDto toDto(RestaurantEntity entity);

    @Mapping(target = "id", ignore = true)
    RestaurantEntity toEntity(RestaurantDto restaurantDto);

    void updateEntityFromDto(RestaurantUpdateDto restaurantDto,
                             @MappingTarget RestaurantEntity restaurantEntity);
}
