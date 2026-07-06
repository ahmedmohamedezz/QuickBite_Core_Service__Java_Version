package com.quickbite.core.restaurant.mapper;

import com.quickbite.core.restaurant.domain.RestaurantEntity;
import com.quickbite.core.restaurant.dto.RestaurantDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {
    RestaurantDto toDto(RestaurantEntity entity);

    @Mapping(target = "id", ignore = true)
    RestaurantEntity toEntity(RestaurantDto restaurantDto);
}
