package com.Ristretto.API.domain.taskList;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;


@Schema(description = "Dados para registro de nova lista de tarefas")
public record TaskListRegistrationData(
        @NotBlank(message = "{title.required}")
        @Schema(description = "Título da lista", example = "Lista Semanal")
        String title,

        @Schema(description = "Cor representativa da lista", example = "#FF5733")
        String color,

        @Schema(description = "Ícone da lista", example = "📌")
        String icon,

        @NotNull(message = "{type.required}")
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

        @NotNull(message = "{user.required}")
        @Schema(description = "ID do usuário dono da lista", example = "1")
        Long userId
) {
        @AssertTrue(message = "Repetition type and amount are required for cyclic lists")
        @JsonIgnore
        public boolean isRepetitionValid() {
                if (type == ListType.CYCLIC) {
                        return repetitionIntervalType != null && repetitionIntervalAmount != null;
                }
                return true;
        }

        @AssertTrue(message = "Deadline is required for standard lists")
        @JsonIgnore
        public boolean isDeadlineValid() {
                if (type == ListType.STANDARD) {
                        return deadline != null;
                }
                return true;
        }
}

