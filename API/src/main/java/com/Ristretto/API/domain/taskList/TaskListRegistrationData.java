package com.Ristretto.API.domain.taskList;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskListRegistrationData(
        @NotBlank(message = "{title.required}")
        String title,
        String color,
        String icon,

        @NotNull(message = "{type.required}")
        ListType type,
        Integer repetitionIntervalAmount,
        IntervalType repetitionIntervalType,
        Boolean repeatWhenComplete,
        Boolean manualRestartAllowed,
        LocalDateTime deadline,
        @NotNull(message = "{user.required}")
        Long userId
){}
