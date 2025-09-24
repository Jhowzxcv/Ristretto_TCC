package com.Ristretto.API.domain.category;

import jakarta.validation.constraints.NotNull;

public record CategoryUpdateData(

   @NotNull
   Long id,
   String name,
   String description,
   String color,
   String icon
) {
}
