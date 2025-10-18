package com.Ristretto.API.domain.taskList;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Schema(description = "Dados para atualização de uma lista de tarefas existente")
public record TaskListUpdateData(
        @NotNull(message = "{id.required}")
        @Positive
        @Schema(description = "ID da lista a ser atualizada", example = "1", required = true)
        Long id,

        @Schema(description = "Título da lista", example = "Lista Atualizada", required = false)
        String title,

        @Schema(description = "Cor representativa da lista", example = "#33FF57", required = false)
        String color,

        @Schema(description = "Ícone da lista", example = "📝", required = false)
        String icon,

        @Schema(description = "Tipo da lista", example = "CYCLIC", required = false)
        ListType type,

        @Schema(description = "Quantidade de intervalo para listas cíclicas", example = "7", required = false)
        Integer repetitionIntervalAmount,

        @Schema(description = "Tipo de intervalo para listas cíclicas", example = "DAY", required = false)
        IntervalType repetitionIntervalType,

        @Schema(description = "Repetir automaticamente quando concluída", example = "true", required = false)
        Boolean repeatWhenComplete,

        @Schema(description = "Permite reinício manual da lista", example = "false", required = false)
        Boolean manualRestartAllowed,

        @Schema(description = "Prazo da lista", example = "2025-12-31T23:59:00", required = false)
        LocalDateTime deadline
) {}