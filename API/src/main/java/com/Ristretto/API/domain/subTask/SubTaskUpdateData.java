package com.Ristretto.API.domain.subTask;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SubTaskUpdateData(
        @NotNull
        Long id,
        String title,
        Boolean isDone,
        String description,
        LocalDateTime dueDate
) {



}
