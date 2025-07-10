package com.Ristretto.API.domain.user;

public enum Theme {
    LIGHT,
    DARK,
    HIGH_CONTRAST;

    public static Theme fromString(String value) {
        return Theme.valueOf(value.toUpperCase());
    }
}
