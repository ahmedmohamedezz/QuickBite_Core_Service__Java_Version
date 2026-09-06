package com.quickbite.core.rbac.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RestaurantMemberCreateDto(
        @Email(message = "invalid email format")
        @NotBlank(message = "email is required")
        String email,

        @NotBlank(message = "name is required")
        String name,

        @NotBlank
        String phoneNumber,

        @NotBlank(message = "role name is required")
        String role,

        List<Long> branchIds
) {
}
