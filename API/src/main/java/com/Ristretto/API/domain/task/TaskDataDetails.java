package com.Ristretto.API.domain.task;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Detalhes de uma tarefa")
public record TaskDataDetails(

        @Schema(description = "ID da tarefa", example = "1")
        Long id,

        @Schema(description = "Título da tarefa", example = "Estudar Java")
        String title,

        @Schema(description = "Descrição detalhada da tarefa", example = "Revisar módulos de OOP")
        String description,

        @Schema(description = "Data de vencimento da tarefa", example = "2025-10-20T15:00:00")
        LocalDateTime dueDate,

        @Schema(description = "Prioridade da tarefa", example = "HIGH")
        Priority priority,

        @Schema(description = "Status da tarefa", example = "PENDING")
        Status status,

        @Schema(description = "ID do usuário dono da tarefa", example = "1")
        Long userId,

        @Schema(description = "ID da categoria associada", example = "2")
        Long categoryId,

        @Schema(description = "ID da meta associada", example = "3")
        Long goalId,

        @Schema(description = "ID da lista de tarefas associada", example = "4")
        Long taskListId,

        @Schema(description = "Indica se a tarefa está em modo foco", example = "false")
        Boolean focusMode,

        @Schema(description = "Indica se a tarefa foi concluída", example = "false")
        Boolean isDone
) {
    public TaskDataDetails(Task task){
        this(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus(),
                task.getUser() != null ? task.getUser().getId() : null,
                task.getCategory() != null ? task.getCategory().getId() : null,
                task.getGoal() != null ? task.getGoal().getId() : null,
                task.getTaskList() != null ? task.getTaskList().getId() : null,
                task.getFocusMode(),
                task.getIsDone()
        );
    }
}
