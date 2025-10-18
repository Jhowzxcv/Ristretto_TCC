package com.Ristretto.API.domain.calendarEvent;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Dados para atualizar um evento existente")
public record CalendarEventUpdateData(
        @NotNull(message = "{id.required}")
        @Positive
        @Schema(description = "ID do evento (chave primária)", example = "1", required = true)
        Long id,

        @Schema(description = "Título do evento", example = "Reunião de trabalho")
        String title,

        @Schema(description = "Descrição do evento", example = "Discutir metas do mês")
        String description,

        @Schema(description = "Data e hora de início do evento", example = "2025-10-20T14:00:00")
        LocalDateTime startDate,

        @Schema(description = "Data e hora de término do evento", example = "2025-10-20T15:00:00")
        LocalDateTime endDate,

        @Schema(description = "Fuso horário do evento", example = "America/Sao_Paulo")
        String timezone,

        @Schema(description = "Cor do evento em hexadecimal", example = "#1A2B3C")
        String color,

        @Schema(description = "Se o evento se repete", example = "true")
        Boolean repetitive,

        @Schema(description = "Tipo de repetição do evento")
        RepetitionType repetitionType,

        @Schema(description = "Regra de repetição do evento", example = "FREQ=WEEKLY;BYDAY=MO")
        String repeitionRule,

        @Schema(description = "Se o evento possui lembrete tátil", example = "false")
        Boolean tactileReminder
) {}
