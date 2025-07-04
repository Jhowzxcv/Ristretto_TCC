package com.Ristretto.API.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRegistrationData(
        @NotBlank(message = "{name.required}")
        String name,

        @NotBlank(message = "{email.required}")
        @Email(message = "{email.invalid}")
        String email,

        @NotBlank(message = "{password.required}")
        String password,

        Theme theme,

        Integer defaultReminderMin,

        String avatarUrl
) {
}