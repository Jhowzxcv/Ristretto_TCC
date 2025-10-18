package com.Ristretto.API.domain.task;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Schema(description = "Dados para registro de uma nova tarefa")
public record TaskRegistrationData(

        @NotBlank(message = "{title.required}")
        @Schema(description = "Título da tarefa", example = "Estudar Java", required = true)
        String title,

        @Schema(description = "Descrição detalhada da tarefa", example = "Revisar módulos de OOP", required = false)
        String description,

        @Future(message = "{dueDate.future}")
        @Schema(description = "Data de vencimento da tarefa", example = "2025-10-20T15:00:00", required = false)
        LocalDateTime dueDate,

        @NotNull(message = "{priority.required}")
        @Schema(description = "Prioridade da tarefa", example = "HIGH", required = true)
        Priority priority,

        @NotNull(message = "{status.required}")
        @Schema(description = "Status da tarefa", example = "PENDING", required = true)
        Status status,

        @NotNull(message = "{user.required}")
        @Schema(description = "ID do usuário dono da tarefa", example = "1", required = true)
        Long userId,

        @Schema(description = "ID da categoria associada à tarefa", example = "2", required = false)
        Long categoryId,

        @Schema(description = "ID da meta associada à tarefa", example = "3", required = false)
        Long goalId,

        @Schema(description = "ID da lista de tarefas associada", example = "4", required = false)
        Long taskListId,

        @Schema(description = "Indica se a tarefa está em modo foco", example = "false", required = false)
        Boolean focusMode,

        @Schema(description = "Indica se a tarefa já foi concluída", example = "false", required = false)
        Boolean isDone
) implements TaskDataCommon {
}