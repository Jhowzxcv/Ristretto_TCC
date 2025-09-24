package com.Ristretto.API.domain.task;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskUpdateData(
        @NotNull
        Long id,
        String title,
        String description,
        LocalDateTime dueDate,
        Priority priority,
        Status status,
        Long categoryId,
        Long goalId,
        Long taskListId,
        Boolean focusMode
) implements TaskDataCommon {
}
