package com.Ristretto.API.infra.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token JWT retornado após login bem-sucedido")
public record JWTTokenData(
        @JsonProperty("token")
        @Schema(description = "Token JWT válido", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {}
