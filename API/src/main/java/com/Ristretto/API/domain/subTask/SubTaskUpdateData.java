package com.Ristretto.API.domain.subTask;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record SubTaskUpdateData(
        @NotNull(message = "{id.required}")
        @Positive
        @Schema(description = "ID da subtarefa a ser atualizada", example = "1", required = true)
        Long id,

        @Schema(description = "Título atualizado da subtarefa", example = "Finalizar relatório revisado")
        String title,

        @Schema(description = "Indica se a subtarefa foi concluída", example = "true")
        Boolean isDone,

        @Schema(description = "Descrição atualizada da subtarefa", example = "Revisar métricas do trimestre")
        String description,

        @Schema(description = "Nova data de vencimento da subtarefa", example = "2025-12-31T23:59:00")
        LocalDateTime dueDate
) {}
