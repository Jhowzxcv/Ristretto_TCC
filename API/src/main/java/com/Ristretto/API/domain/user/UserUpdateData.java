package com.Ristretto.API.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(name = "UserUpdateData", description = "Dados para atualizar um usuário existente")
public record UserUpdateData(
        @NotNull(message = "{id.required}")
        @Positive
        @Schema(description = "ID do usuário a ser atualizado", example = "1")
        Long id,
        @Schema(description = "Nome do usuário", example = "Jhonathan Dourado")
        String name,
        @Schema(description = "Tema preferido do usuário", example = "DARK")
        Theme theme,
        @Schema(description = "Tempo padrão de lembrete em minutos", example = "10")
        Integer defaultReminderMin,
        @Schema(description = "URL do avatar do usuário")
        String avatarUrl
) {}
