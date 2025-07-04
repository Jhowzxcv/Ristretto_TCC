package com.Ristretto.API.domain.subTask;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubTaskRegistrationData(
        @NotNull(message = "{task.required}")
        Long taskId,

        @NotBlank(message = "{title.required}")
        String title,

        Boolean isDone,

        String description
) {
}