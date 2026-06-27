package com.quickbite.core.user.mapper;

import com.quickbite.core.auth.dto.UserRegisterDto;
import com.quickbite.core.user.domain.UserEntity;
import com.quickbite.core.user.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(UserEntity user);

    @Mapping(target = "id", ignore = true)
    UserEntity toEntity(UserDto userDto);
}
