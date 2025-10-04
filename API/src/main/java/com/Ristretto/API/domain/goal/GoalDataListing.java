package com.Ristretto.API.domain.goal;

import java.time.LocalDateTime;

public record GoalDataListing(
        Long id,
        String title,
        String description,
        LocalDateTime creationDate,
        LocalDateTime dueDate
) {
    public GoalDataListing(Goal goal) {
        this(
                goal.getId(),
                goal.getTitle(),
                goal.getDescription(),
                goal.getCreationDate(),
                goal.getDueDate()
        );
    }
}