package com.quickbite.core.user.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SystemRole {
    CUSTOMER("customer"),
    DELIVERY_AGENT("delivery_agent"),
    SYSTEM_ADMIN("system_admin"),
    RESTAURANT_USER("restaurant_user"),;

    private final String value;

    SystemRole(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
