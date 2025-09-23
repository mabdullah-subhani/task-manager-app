package com.demoproject.authservice.entity;

import java.util.Arrays;

public enum Role {
    USER,
    ADMIN,
    MANAGER;

    public static Role fromStringIgnoreCase(String value) {
        return Arrays.stream(Role.values())
                .filter(role -> role.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + value));
    }
}
