package com.Ristretto.API.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;


@Schema(name = "UserDataDetails", description = "Detalhes de um usuário")
public record UserDataDetails(
        @Schema(description = "ID do usuário", example = "1")
        Long id,
        @Schema(description = "Nome do usuário", example = "Jhonathan Dourado")
        String name,
        @Schema(description = "Tema preferido do usuário", example = "DARK")
        Theme theme,
        @Schema(description = "Tempo padrão de lembrete em minutos", example = "10")
        Integer defaultReminderMin,
        @Schema(description = "URL do avatar do usuário")
        String avatarUrl,
        @Schema(description = "Data do último login")
        LocalDateTime lastLogin,
        @Schema(description = "Data de criação do usuário")
        LocalDateTime createdAt,
        @Schema(description = "Data da última atualização do usuário")
        LocalDateTime updatedAt
) {
    public UserDataDetails(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getTheme(),
                user.getDefaultReminderMin(),
                user.getAvatarUrl(),
                user.getLastLogin(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
