package com.quickbite.core.retstaurant_branches.mapper;

import com.quickbite.core.retstaurant_branches.domain.BranchEntity;
import com.quickbite.core.retstaurant_branches.dto.BranchDto;
import com.quickbite.core.retstaurant_branches.dto.BranchUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BranchMapper {
    @Mapping(target = "restaurantId", source = "restaurant.id")
    BranchDto toDto(BranchEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant.id", source = "restaurantId")
    BranchEntity toEntity(BranchDto dto);

    void updateEntityFromDto(BranchUpdateDto dto,
                             @MappingTarget BranchEntity entity);
}
