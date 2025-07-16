package com.Ristretto.API.domain.calendarEvent;

import java.time.LocalDateTime;

public record CalendarEventDataListing(

        Long id,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String timezone,
        String color,
        Boolean repetitive,
        RepetitionType repetitionType,
        String  repetitionRule,
        Boolean tactileReminder
        ) {
    public CalendarEventDataListing(CalendarEvent calendarEvent){
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
                calendarEvent.getTactileReminder()
        );
    }
}
