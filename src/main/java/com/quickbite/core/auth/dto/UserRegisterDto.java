package com.quickbite.core.auth.dto;

import com.quickbite.core.restaurant.dto.RestaurantRegisterDto;
import com.quickbite.core.user.enums.SystemRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record UserRegisterDto(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Phone number is required")
        @Size(min = 9, max = 11)
        String phone,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&].{8,}$",
                message = "Password is not strong enough. It must contain at least 8 characters, one uppercase letter, one lowercase letter, one number, and one symbol."
        )
        String password,

        @NotNull(message = "Role is required")
        SystemRole role,
        @Valid
        RestaurantRegisterDto restaurant
) {
}
