package com.Ristretto.API.domain.goal;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Dados para registrar uma nova meta")
public record GoalRegistrationData(
        @NotBlank(message = "{title.required}")
        @Schema(description = "Título da meta", example = "Finalizar relatório", required = true)
        String title,

        @Schema(description = "Descrição detalhada da meta", example = "Finalizar o relatório financeiro até o fim do mês", required = false)
        String description,

        @Future(message = "{dueDate.future}")
        @Schema(description = "Data limite para conclusão da meta", example = "2025-10-31T18:00:00", required = false)
        LocalDateTime dueDate,

        @NotNull(message = "{user.required}")
        @Schema(description = "ID do usuário dono da meta", example = "1", required = true)
        Long userId
) {}