package com.Ristretto.API.domain.calendarEvent;

import com.Ristretto.API.validation.ChronologicalDates;
import com.Ristretto.API.validation.ValidRRule;
import com.Ristretto.API.validation.ValidTimezone;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@ChronologicalDates(start = "startDate", end = "endDate")
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

        @ValidTimezone
        String timezone,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Cor deve estar no formato hexadecimal ex: #1A2B3C")
        String color,

        @NotNull(message = "{user.required}")
        Long userId,

        Long taskId,

        Boolean repetitive,

        RepetitionType repetitionType,

        @ValidRRule
        String repetitionRule,

        Boolean tactileReminder
) {
}