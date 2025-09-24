package com.Ristretto.API.domain.category;

import java.time.LocalDateTime;

public record CategoryDataListing(
        Long id,
        String name,
        String description,
        String color,
        String icon,
        LocalDateTime createdAt
) {
    public CategoryDataListing(Category category){
        this(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getColor(),
                category.getIcon(),
                category.getCreatedAt()
        );
    }
}

