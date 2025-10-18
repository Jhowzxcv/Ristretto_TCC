package com.Ristretto.API.domain.taskList;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Resumo de uma lista de tarefas para listagem")
public record TaskListDataListing(
        @Schema(description = "ID da lista", example = "1")
        Long id,

        @Schema(description = "Título da lista", example = "Lista Semanal")
        String title,

        @Schema(description = "Cor representativa da lista", example = "#FF5733")
        String color,

        @Schema(description = "Ícone da lista", example = "📌")
        String icon,

        @Schema(description = "Tipo da lista", example = "STANDARD")
        ListType type,

        @Schema(description = "Prazo da lista", example = "2025-12-31T23:59:00")
        LocalDateTime deadline
) {
    public TaskListDataListing(TaskList taskList) {
        this(
                taskList.getId(),
                taskList.getTitle(),
                taskList.getColor(),
                taskList.getIcon(),
                taskList.getType(),
                taskList.getDeadline()
        );
    }
}