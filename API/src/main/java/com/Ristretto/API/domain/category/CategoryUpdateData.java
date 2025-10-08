package com.Ristretto.API.domain.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CategoryUpdateData(
   @NotNull(message = "{id.required}")
   @Positive
   Long id,
   String name,
   String description,
   String color,
   String icon
) {
}
