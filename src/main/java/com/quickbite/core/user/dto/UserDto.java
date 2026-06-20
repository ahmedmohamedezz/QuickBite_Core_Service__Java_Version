package com.quickbite.core.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.user.domain.UserEntity;
import com.quickbite.core.user.enums.SystemRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private Long id;
    private String email;
    private String phone;
    private String name;
    private SystemRole systemRole;
    private LocalDateTime createdAt;

    public static UserDto fromEntity(UserEntity entity) {
        if (entity == null) return null;

        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .name(entity.getName())
                .systemRole(entity.getSystemRole())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}