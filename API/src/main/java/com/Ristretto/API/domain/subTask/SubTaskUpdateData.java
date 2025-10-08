package com.Ristretto.API.domain.subTask;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record SubTaskUpdateData(
        @NotNull(message = "{id.required}")
        @Positive
        Long id,
        String title,
        Boolean isDone,
        String description,
        LocalDateTime dueDate
) {



}
