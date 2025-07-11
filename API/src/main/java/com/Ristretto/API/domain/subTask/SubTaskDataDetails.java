package com.Ristretto.API.domain.subTask;

import java.time.LocalDateTime;

public record SubTaskDataDetails(
        Long id,
        String title,
        boolean isDone,
        String description,
        LocalDateTime dueDate,
        Long taskId
) {
    public SubTaskDataDetails(SubTask subTask){
        this(
                subTask.getId(),
                subTask.getTitle(),
                subTask.getIsDone(),
                subTask.getDescription(),
                subTask.getDueDate(),
                subTask.getTask() != null ? subTask.getTask().getId() : null
        );
    }
}
