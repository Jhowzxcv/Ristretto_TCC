package com.Ristretto.API.domain.goal;

import java.time.LocalDateTime;

public record GoalDataDetails(
        Long id,
        String title,
        String description,
        LocalDateTime creationDate,
        LocalDateTime dueDate,
        Long userId
) {
    public GoalDataDetails(Goal goal) {
        this(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getCreationDate(),
                goal.getDueDate(),
                goal.getUser() != null ? goal.getUser().getId() : null
        );
    }
}