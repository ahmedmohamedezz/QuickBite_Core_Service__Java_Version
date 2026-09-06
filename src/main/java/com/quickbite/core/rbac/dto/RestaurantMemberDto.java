package com.quickbite.core.rbac.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.rbac.enums.MemberStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RestaurantMemberDto(
        Long id,
        Long restaurantId,
        Long userId,
        Long roleId,
        MemberStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
