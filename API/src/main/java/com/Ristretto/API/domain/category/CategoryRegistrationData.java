package com.Ristretto.API.domain.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CategoryRegistrationData(
        @NotBlank(message = "{name.required}")
        String name,

        String description,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "{color.invalid}")        String color,

        String icon,

        @NotNull(message = "{user.required}")
        Long userId
) {
}