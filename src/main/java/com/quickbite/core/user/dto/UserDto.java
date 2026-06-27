package com.quickbite.core.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.user.enums.SystemRole;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDto(
        Long id,
        String email,
        String phone,
        String name,
        SystemRole systemRole,
        LocalDateTime createdAt
) {
}