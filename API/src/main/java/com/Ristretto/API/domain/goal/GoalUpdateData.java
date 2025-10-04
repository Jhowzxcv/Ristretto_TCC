package com.Ristretto.API.domain.goal;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record GoalUpdateData(
        @NotNull
        Long id,
        String title,
        String description,
        LocalDateTime dueDate
) {
}