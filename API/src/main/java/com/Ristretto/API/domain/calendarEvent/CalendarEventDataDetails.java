package com.Ristretto.API.domain.calendarEvent;

import java.time.LocalDateTime;

public record CalendarEventDataDetails(
        Long id,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String timezone,
        String color,
        Boolean repetitive,
        RepetitionType repetitionType,
        String repetitionRule,
        Boolean tactileReminder,
        Long userID,
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
