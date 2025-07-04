package com.Ristretto.API.domain.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;

public record TaskRegistrationData(
        @NotBlank(message = "{title.required}")
        String title,

        String description,

        @Future(message = "{dueDate.future}")
        LocalDateTime dueDate,

        @NotNull(message = "{priority.required}")
        Priority priority,

        @NotNull(message = "{status.required}")
        Status status,

        @NotNull(message = "{user.required}")
        Long userId,

        Long categoryId,

        Long goalId,

        Boolean focusMode,

        Boolean isDone
) {
}