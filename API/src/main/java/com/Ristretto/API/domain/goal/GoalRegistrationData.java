package com.Ristretto.API.domain.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;

public record GoalRegistrationData(
        @NotBlank(message = "{title.required}")
        String title,

        String description,

        @Future(message = "{dueDate.future}")
        LocalDateTime dueDate,

        @NotNull(message = "{user.required}")
        Long userId
) {
}