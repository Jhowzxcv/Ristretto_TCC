package com.Ristretto.API.domain.calendarEvent;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Detalhes completos de um evento")
public record CalendarEventDataDetails(
        @Schema(description = "ID do evento (chave primária)", example = "1")
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
        String repetitionRule,

        @Schema(description = "Se o evento possui lembrete tátil", example = "false")
        Boolean tactileReminder,

        @Schema(description = "ID do usuário dono do evento", example = "1")
        Long userID,

        @Schema(description = "ID da tarefa associada ao evento", example = "2")
        Long taskId
) {
    public CalendarEventDataDetails(CalendarEvent calendarEvent){
        this(
                calendarEvent.getId(),
                calendarEvent.getTitle(),
                calendarEvent.getDescription(),
                calendarEvent.getStartDate(),
                calendarEvent.getEndDate(),
                calendarEvent.getTimezone(),
                calendarEvent.getColor(),
                calendarEvent.getRepetitive(),
                calendarEvent.getRepetitionType(),
                calendarEvent.getRepetitionRule(),
                calendarEvent.getTactileReminder(),
                calendarEvent.getUser() != null ? calendarEvent.getUser().getId() : null,
                calendarEvent.getTask() != null ? calendarEvent.getTask().getId() : null
        );
    }
}
