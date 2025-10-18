package com.Ristretto.API.domain.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Dados para atualizar uma meta existente")
public record GoalUpdateData(
        @NotNull(message = "{id.required}")
        @Positive
        @Schema(description = "ID da meta (chave primária)", example = "1", required = true)
        Long id,

        @Schema(description = "Título da meta", example = "Finalizar relatório", required = false)
        String title,

        @Schema(description = "Descrição detalhada da meta", example = "Finalizar o relatório financeiro até o fim do mês", required = false)
        String description,

        @Schema(description = "Nova data limite para conclusão da meta", example = "2025-10-31T18:00:00", required = false)
        LocalDateTime dueDate
) {}