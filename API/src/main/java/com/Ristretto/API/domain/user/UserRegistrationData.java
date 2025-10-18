package com.Ristretto.API.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "UserRegistrationData", description = "Dados para registrar um novo usuário")
public record UserRegistrationData(
        @NotBlank(message = "{name.required}")
        @Schema(description = "Nome do usuário", example = "Jhonathan Dourado", required = true)
        String name,
        @NotBlank(message = "{email.required}")
        @Email(message = "{email.invalid}")
        @Schema(description = "E-mail do usuário", example = "jhonathan@example.com", required = true)
        String email,
        @NotBlank(message = "{password.required}")
        @Schema(description = "Senha do usuário", example = "123456", required = true)
        String password,
        @Schema(description = "Tema preferido do usuário", example = "DARK", required = false)
        Theme theme,
        @Schema(description = "Tempo padrão de lembrete em minutos", example = "10", required = false)
        Integer defaultReminderMin,
        @Schema(description = "URL do avatar do usuário", required = false)
        String avatarUrl
) {}