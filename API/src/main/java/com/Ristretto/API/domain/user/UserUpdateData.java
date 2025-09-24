package com.Ristretto.API.domain.user;

import jakarta.validation.constraints.NotNull;

public record UserUpdateData(
        @NotNull
        Long id,
        String name,
        Theme theme,
        Integer defaultReminderMin,
        String avatarUrl){

}
