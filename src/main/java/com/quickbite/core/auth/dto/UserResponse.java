package com.quickbite.core.auth.dto;

import com.quickbite.core.user.domain.UserEntity;
import com.quickbite.core.user.enums.SystemRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String phone;
    private String name;
    private SystemRole systemRole;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(UserEntity entity) {
        if (entity == null) return null;

        return UserResponse.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .name(entity.getName())
                .systemRole(entity.getSystemRole())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}