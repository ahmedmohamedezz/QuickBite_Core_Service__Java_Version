package com.quickbite.core.auth.dto;

import java.util.List;

public record JwtPayload(
        Long userId,
        String email,
        String role,
        Long restaurantId,
        String restaurantRole,
        List<Long> branchIds
) {
}
