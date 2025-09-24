package com.Ristretto.API.domain.subTask;

import com.Ristretto.API.domain.task.Priority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SubTaskRegistrationData(

        @NotBlank(message = "{title.required}")
        String title,

        String description,

        @Future(message = "{dueDate.future}")
        LocalDateTime dueDate,

        @NotNull(message = "{task.required}")
        Long taskId
) {
}