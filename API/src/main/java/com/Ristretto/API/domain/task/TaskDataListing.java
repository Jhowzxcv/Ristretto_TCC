package com.Ristretto.API.domain.task;

import java.time.LocalDateTime;

public record TaskDataListing(Long id, String title, String description, LocalDateTime creationDate, LocalDateTime dueDate, Priority priority, Status status, boolean isDone) {

    public TaskDataListing(Task task){
        this(task.getId(), task.getTitle(), task.getDescription(), task.getCreationDate(),task.getDueDate(),task.getPriority(), task.getStatus(), task.getIsDone());
    }
}
