package com.quickbite.core.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ForgetPasswordDto(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {
}
