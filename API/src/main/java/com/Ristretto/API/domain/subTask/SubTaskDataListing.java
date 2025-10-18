package com.Ristretto.API.domain.subTask;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record SubTaskDataListing(
        @Schema(description = "ID da subtarefa", example = "1", required = true)
        Long id,

        @Schema(description = "Título da subtarefa", example = "Finalizar relatório", required = true)
        String title,

        @Schema(description = "Indica se a subtarefa foi concluída", example = "false")
        Boolean isDone,

        @Schema(description = "Descrição detalhada da subtarefa", example = "Incluir todas as métricas do trimestre")
        String description,

        @Schema(description = "Data de vencimento da subtarefa", example = "2025-12-31T23:59:00")
        LocalDateTime dueDate
) {
    public SubTaskDataListing(SubTask subTask){
        this(
                subTask.getId(),
                subTask.getTitle(),
                subTask.getIsDone(),
                subTask.getDescription(),
                subTask.getDueDate()
        );
    }
}
