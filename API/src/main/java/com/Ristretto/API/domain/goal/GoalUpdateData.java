package com.Ristretto.API.domain.goal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record GoalUpdateData(
        @NotNull(message = "{id.required}")
        @Positive
        Long id,
        String title,
        String description,
        LocalDateTime dueDate
) {
}