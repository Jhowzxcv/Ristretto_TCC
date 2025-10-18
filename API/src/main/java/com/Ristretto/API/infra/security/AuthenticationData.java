package com.Ristretto.API.infra.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para autenticação do usuário")
public record AuthenticationData(
        @NotBlank(message = "{email.required}")
        @Email(message = "{email.invalid}")
        @Schema(description = "Email do usuário", example = "usuario@exemplo.com", required = true)
        String email,

        @NotBlank(message = "{password.required}")
        @Size(min = 6, message = "{password.short}")
        @Schema(description = "Senha do usuário (mínimo 6 caracteres)", example = "senha123", required = true)
        String password
) {}
