package com.Ristretto.API.domain.category;

import java.time.LocalDateTime;

public record CategoryDataDetails(
        Long id,
        String name,
        String description,
        String color,
        String icon,
        LocalDateTime createdAt,
        Long userID
) {
    public CategoryDataDetails(Category category){
        this(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getColor(),
                category.getIcon(),
                category.getCreatedAt(),
                category.getUser() != null ? category.getUser().getId() : null
        );
    }
}
