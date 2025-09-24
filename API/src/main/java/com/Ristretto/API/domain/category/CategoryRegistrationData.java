package com.Ristretto.API.domain.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRegistrationData(
        @NotBlank(message = "{name.required}")
        String name,

        String description,

        String color,

        String icon,

        @NotNull(message = "{user.required}")
        Long userId
) {
}