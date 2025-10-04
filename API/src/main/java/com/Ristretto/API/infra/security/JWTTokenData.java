package com.Ristretto.API.infra.security;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JWTTokenData(
        @JsonProperty("token")
        String token
) {}