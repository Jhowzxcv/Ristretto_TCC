package com.Ristretto.API.domain.calendarEvent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record CalendarEventRegistrationData(
        @NotBlank(message = "{title.required}")
        String title,

        String description,

        @NotNull(message = "{startDate.required}")
        @Future(message = "{startDate.future}")
        LocalDateTime startDate,

        @NotNull(message = "{endDate.required}")
        @Future(message = "{endDate.future}")
        LocalDateTime endDate,

        String timezone,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Cor deve estar no formato hexadecimal ex: #1A2B3C")
        String color,

        @NotNull(message = "{user.required}")
        Long userId,

        Long taskId,

        Boolean repetitive,

        RepetitionType repetitionType,

        String repetitionRule,

        Boolean tactileReminder
) {
}