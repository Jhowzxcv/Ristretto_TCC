package com.Ristretto.API.domain.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UserUpdateData(
        @NotNull(message = "{id.required}")
        @Positive
        Long id,
        String name,
        Theme theme,
        Integer defaultReminderMin,
        String avatarUrl){

}
