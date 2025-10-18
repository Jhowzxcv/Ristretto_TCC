package com.Ristretto.API.domain.category;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Detalhes completos de uma categoria")
public record CategoryDataDetails(
        @Schema(description = "ID da categoria (chave primária)", example = "1")
        Long id,

        @Schema(description = "Nome da categoria", example = "Trabalho")
        String name,

        @Schema(description = "Descrição da categoria", example = "Atividades profissionais")
        String description,

        @Schema(description = "Cor da categoria em hexadecimal", example = "#FF5733")
        String color,

        @Schema(description = "Ícone da categoria", example = "briefcase")
        String icon,

        @Schema(description = "Data de criação da categoria", example = "2025-10-17T12:00:00")
        LocalDateTime createdAt,

        @Schema(description = "ID do usuário dono da categoria", example = "1")
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
