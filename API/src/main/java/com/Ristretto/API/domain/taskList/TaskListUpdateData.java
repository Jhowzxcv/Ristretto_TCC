package com.Ristretto.API.domain.taskList;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record TaskListUpdateData(
        @NotNull(message = "{id.required}")
        @Positive
        Long id,
        String title,
        String color,
        String icon,
        ListType type,
        Integer repetitionIntervalAmount,
        IntervalType repetitionIntervalType,
        Boolean repeatWhenComplete,
        Boolean manualRestartAllowed,
        LocalDateTime deadline
) {}
