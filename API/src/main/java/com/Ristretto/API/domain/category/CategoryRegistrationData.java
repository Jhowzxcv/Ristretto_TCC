package com.Ristretto.API.domain.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados para criar uma nova categoria")
public record CategoryRegistrationData(
        @NotBlank(message = "{name.required}")
        @Schema(description = "Nome da categoria", example = "Trabalho", required = true)
        String name,

        @Schema(description = "Descrição da categoria", example = "Atividades profissionais", required = false)
        String description,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "{color.invalid}")
        @Schema(description = "Cor da categoria em hexadecimal", example = "#FF5733", required = false)
        String color,

        @Schema(description = "Ícone da categoria", example = "briefcase", required = false)
        String icon,

        @NotNull(message = "{user.required}")
        @Schema(description = "ID do usuário dono da categoria", example = "1", required = true)
        Long userId
) {}