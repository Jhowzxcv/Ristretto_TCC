package com.Ristretto.API.domain.task;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record TaskUpdateData(
        @NotNull(message = "{id.required}")
        @Positive
        Long id,
        String title,
        String description,
        @Future
        LocalDateTime dueDate,
        Priority priority,
        Status status,
        Long categoryId,
        Long goalId,
        Long taskListId,
        Boolean focusMode
) implements TaskDataCommon {
}
