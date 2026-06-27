package com.quickbite.core.customer_addresses.mapper;

import com.quickbite.core.customer_addresses.domain.CustomerAddressEntity;
import com.quickbite.core.customer_addresses.dto.CustomerAddressDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")  // make it a spring component @Component
public interface CustomerAddressMapper {
    CustomerAddressDto toDto(CustomerAddressEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    CustomerAddressEntity toEntity(CustomerAddressDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(CustomerAddressDto dto, @MappingTarget CustomerAddressEntity entity);
}
