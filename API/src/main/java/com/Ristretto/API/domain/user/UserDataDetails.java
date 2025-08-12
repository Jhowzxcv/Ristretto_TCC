package com.Ristretto.API.domain.user;

import java.time.LocalDateTime;

public record UserDataDetails(Long id, String name, Theme theme, Integer defaultReminderMin, String avatarUrl, LocalDateTime lastLogin, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public UserDataDetails(User user){
        this(user.getId(), user.getName(), user.getTheme(), user.getDefaultReminderMin(), user.getAvatarUrl(), user.getLastLogin(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
