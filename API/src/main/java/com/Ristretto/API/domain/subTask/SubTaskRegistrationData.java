package com.Ristretto.API.domain.subTask;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SubTaskRegistrationData(
        @NotBlank(message = "{title.required}")
        @Schema(description = "Título da subtarefa", example = "Finalizar relatório", required = true)
        String title,

        @Schema(description = "Descrição detalhada da subtarefa", example = "Incluir todas as métricas do trimestre")
        String description,

        @Future(message = "{dueDate.future}")
        @Schema(description = "Data de vencimento da subtarefa", example = "2025-12-31T23:59:00", required = true)
        LocalDateTime dueDate,

        @NotNull(message = "{task.required}")
        @Schema(description = "ID da tarefa pai à qual esta subtarefa pertence", example = "1", required = true)
        Long taskId
) {}
