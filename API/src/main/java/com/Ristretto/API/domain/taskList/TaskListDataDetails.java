package com.Ristretto.API.domain.taskList;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Detalhes completos de uma lista de tarefas")
public record TaskListDataDetails(
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

        @Schema(description = "Quantidade de intervalo para listas cíclicas", example = "7")
        Integer repetitionIntervalAmount,

        @Schema(description = "Tipo de intervalo para listas cíclicas", example = "DAY")
        IntervalType repetitionIntervalType,

        @Schema(description = "Repetir automaticamente quando concluída", example = "true")
        Boolean repeatWhenComplete,

        @Schema(description = "Permite reinício manual da lista", example = "false")
        Boolean manualRestartAllowed,

        @Schema(description = "Prazo da lista", example = "2025-12-31T23:59:00")
        LocalDateTime deadline,

        @Schema(description = "Data de criação da lista", example = "2025-01-01T10:00:00")
        LocalDateTime creationDate,

        @Schema(description = "ID do usuário dono da lista", example = "1")
        Long userId
) {
    public TaskListDataDetails(TaskList taskList) {
        this(
                taskList.getId(),
                taskList.getTitle(),
                taskList.getColor(),
                taskList.getIcon(),
                taskList.getType(),
                taskList.getRepetitionIntervalAmount(),
                taskList.getRepetitionIntervalType(),
                taskList.getRepeatWhenComplete(),
                taskList.getManualRestartAllowed(),
                taskList.getDeadline(),
                taskList.getCreationDate(),
                taskList.getUser() != null ? taskList.getUser().getId() : null
        );
    }
}
