package com.Ristretto.API.domain.task;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Listagem resumida de tarefas")
public record TaskDataListing(

        @Schema(description = "ID da tarefa", example = "1")
        Long id,

        @Schema(description = "Título da tarefa", example = "Estudar Java")
        String title,

        @Schema(description = "Descrição detalhada da tarefa", example = "Revisar módulos de OOP")
        String description,

        @Schema(description = "Data de criação da tarefa", example = "2025-10-10T10:00:00")
        LocalDateTime creationDate,

        @Schema(description = "Data de vencimento da tarefa", example = "2025-10-20T15:00:00")
        LocalDateTime dueDate,

        @Schema(description = "Prioridade da tarefa", example = "HIGH")
        Priority priority,

        @Schema(description = "Status da tarefa", example = "PENDING")
        Status status,

        @Schema(description = "Indica se a tarefa foi concluída", example = "false")
        boolean isDone
) {
    public TaskDataListing(Task task){
        this(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getCreationDate(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus(),
                task.getIsDone()
        );
    }
}
