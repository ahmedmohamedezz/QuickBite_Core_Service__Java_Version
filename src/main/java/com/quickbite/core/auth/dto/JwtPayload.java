package com.quickbite.core.auth.dto;

public record JwtPayload(Long userId, String email, String role) {
}
