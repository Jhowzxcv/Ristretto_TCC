package com.Ristretto.API.domain.task;

import java.time.LocalDateTime;

public record TaskDataDetails(Long id, String title, String description, LocalDateTime dueDate, Priority priority, Status status, Long UserId, Long categoryId, Long goalId, Boolean focusMode, Boolean isDone ){
    public TaskDataDetails(Task task) {
        this(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus(),
                task.getUser() != null ? task.getUser().getId() : null,
                task.getCategory() != null ? task.getCategory().getId() : null,
                task.getGoal() != null ? task.getGoal().getId() : null,
                task.getFocusMode(),
                task.getIsDone()
        );
    }

}
