package com.Ristretto.API.domain.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para atualizar uma categoria existente")
public record CategoryUpdateData(
        @NotNull(message = "{id.required}")
        @Positive
        @Schema(description = "ID da categoria (chave primária)", example = "1", required = true)
        Long id,

        @Schema(description = "Nome da categoria", example = "Trabalho", required = false)
        String name,

        @Schema(description = "Descrição da categoria", example = "Atividades profissionais", required = false)
        String description,

        @Schema(description = "Cor da categoria em hexadecimal", example = "#FF5733", required = false)
        String color,

        @Schema(description = "Ícone da categoria", example = "briefcase", required = false)
        String icon
) {}