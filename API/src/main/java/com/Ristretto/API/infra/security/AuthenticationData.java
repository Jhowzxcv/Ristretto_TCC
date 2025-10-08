package com.Ristretto.API.infra.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationData(
        @NotBlank(message = "{email.required}")
        @Email(message = "{email.invalid}")

        String email,

        @NotBlank(message = "{password.required}")
        @Size(min = 6, message = "{password.short}")
        String password
) {}