package com.Ristretto.API.domain.calendarEvent;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CalendarEventUpdateData(
        @NotNull
        Long id,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String timezone,
        String color,
        Boolean repetitive,
        RepetitionType repetitionType,
        String repeitionRule,
        Boolean tactileReminder
) {
}
