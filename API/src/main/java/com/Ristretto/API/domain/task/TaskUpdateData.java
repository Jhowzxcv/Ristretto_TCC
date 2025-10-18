package com.Ristretto.API.domain.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Schema(description = "Dados para atualização de uma tarefa existente")
public record TaskUpdateData(

        @NotNull(message = "{id.required}")
        @Positive
        @Schema(description = "ID da tarefa", example = "1", required = true)
        Long id,

        @Schema(description = "Título da tarefa", example = "Estudar Java", required = false)
        String title,

        @Schema(description = "Descrição detalhada da tarefa", example = "Revisar módulos de OOP", required = false)
        String description,

        @Future(message = "{dueDate.future}")
        @Schema(description = "Nova data de vencimento da tarefa", example = "2025-10-20T15:00:00", required = false)
        LocalDateTime dueDate,

        @Schema(description = "Nova prioridade da tarefa", example = "MEDIUM", required = false)
        Priority priority,

        @Schema(description = "Novo status da tarefa", example = "IN_PROGRESS", required = false)
        Status status,

        @Schema(description = "Novo ID da categoria associada à tarefa", example = "2", required = false)
        Long categoryId,

        @Schema(description = "Novo ID da meta associada à tarefa", example = "3", required = false)
        Long goalId,

        @Schema(description = "Novo ID da lista de tarefas associada", example = "4", required = false)
        Long taskListId,

        @Schema(description = "Atualiza se a tarefa está em modo foco", example = "true", required = false)
        Boolean focusMode
) implements TaskDataCommon {
}