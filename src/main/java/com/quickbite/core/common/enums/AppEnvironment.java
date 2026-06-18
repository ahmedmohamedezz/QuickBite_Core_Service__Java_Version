package com.quickbite.core.common.enums;

public enum AppEnvironment {
    DEVELOPMENT("development"),
    PRODUCTION("production");

    private final String value;

    AppEnvironment(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AppEnvironment fromString(String text) {
        for (AppEnvironment appEnvironment : AppEnvironment.values()) {
            if (appEnvironment.value.equalsIgnoreCase(text) || appEnvironment.name().equalsIgnoreCase(text)) {
                return appEnvironment;
            }
        }
        return null;
    }
}
