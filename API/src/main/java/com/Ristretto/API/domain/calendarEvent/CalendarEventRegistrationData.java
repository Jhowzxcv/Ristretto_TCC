package com.Ristretto.API.domain.calendarEvent;

import com.Ristretto.API.validation.ChronologicalDates;
import com.Ristretto.API.validation.ValidRRule;
import com.Ristretto.API.validation.ValidTimezone;
import com.Ristretto.API.validation.ConsistentRepetition;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@ChronologicalDates(start = "startDate", end = "endDate")
@ConsistentRepetition
@Schema(description = "Dados para registrar um novo evento no calendário")
public record CalendarEventRegistrationData(
        @NotBlank(message = "{title.required}")
        @Schema(description = "Título do evento", example = "Reunião de trabalho", required = true)
        String title,

        @Schema(description = "Descrição opcional do evento", example = "Discutir metas do mês")
        String description,

        @NotNull(message = "{startDate.required}")
        @Future(message = "{startDate.future}")
        @Schema(description = "Data e hora de início do evento", example = "2025-10-20T14:00:00", required = true)
        LocalDateTime startDate,

        @NotNull(message = "{endDate.required}")
        @Future(message = "{endDate.future}")
        @Schema(description = "Data e hora de término do evento", example = "2025-10-20T15:00:00", required = true)
        LocalDateTime endDate,

        @ValidTimezone
        @Schema(description = "Fuso horário do evento", example = "America/Sao_Paulo")
        String timezone,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Cor deve estar no formato hexadecimal ex: #1A2B3C")
        @Schema(description = "Cor do evento em hexadecimal", example = "#1A2B3C")
        String color,

        @NotNull(message = "{user.required}")
        @Schema(description = "ID do usuário dono do evento", example = "1", required = true)
        Long userId,

        @Schema(description = "ID da tarefa associada ao evento", example = "2")
        Long taskId,

        @Schema(description = "Se o evento se repete", example = "true")
        Boolean repetitive,

        @Schema(description = "Tipo de repetição do evento")
        RepetitionType repetitionType,

        @ValidRRule
        @Schema(description = "Regra de repetição do evento", example = "FREQ=WEEKLY;BYDAY=MO")
        String repetitionRule,

        @Schema(description = "Se o evento possui lembrete tátil", example = "false")
        Boolean tactileReminder
) {}
