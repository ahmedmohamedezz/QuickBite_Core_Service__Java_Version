package com.quickbite.core.common.security;

public class UserContext {
    private static final ThreadLocal<AuthenticatedUser> current = new ThreadLocal<>();

    public record AuthenticatedUser(Long id, String email, String role) {}

    public static void set(AuthenticatedUser user) {
        current.set(user);
    }

    public static AuthenticatedUser get() {
        return current.get();
    }

    public static void clear() {
        current.remove();
    }
}