package com.Ristretto.API.domain.taskList;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskListUpdateData(
        @NotNull(message = "{id.required}")
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
