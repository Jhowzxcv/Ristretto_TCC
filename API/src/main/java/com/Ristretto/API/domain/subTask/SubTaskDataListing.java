package com.Ristretto.API.domain.subTask;

import java.time.LocalDateTime;

public record SubTaskDataListing(
        Long id,
        String title,
        Boolean isDone,
        String description,
        LocalDateTime dueDate
) {
    public SubTaskDataListing(SubTask subTask){
        this(subTask.getId(), subTask.getTitle(), subTask.getIsDone(), subTask.getDescription(), subTask.getDueDate());
    }
}
