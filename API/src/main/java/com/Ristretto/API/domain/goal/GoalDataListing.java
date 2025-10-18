package com.Ristretto.API.domain.goal;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Informações resumidas de uma meta")
public record GoalDataListing(
        @Schema(description = "ID da meta (chave primária)", example = "1")
        Long id,

        @Schema(description = "Título da meta", example = "Finalizar relatório")
        String title,

        @Schema(description = "Descrição detalhada da meta", example = "Finalizar o relatório financeiro até o fim do mês")
        String description,

        @Schema(description = "Data de criação da meta", example = "2025-10-17T12:00:00")
        LocalDateTime creationDate,

        @Schema(description = "Data limite para conclusão da meta", example = "2025-10-31T18:00:00")
        LocalDateTime dueDate
) {

    public GoalDataListing(Goal goal) {
        this(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getCreationDate(),
                goal.getDueDate()
        );
    }
}