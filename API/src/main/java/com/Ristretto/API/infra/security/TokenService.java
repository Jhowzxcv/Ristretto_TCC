package com.Ristretto.API.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.Ristretto.API.domain.user.User;
import com.Ristretto.API.infra.exception.Token.TokenGenerationException;
import com.Ristretto.API.infra.exception.Token.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration-hours:2}")
    private Long expirationHours;

    private static final String ISSUER = "API Ristretto";

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getEmail())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(expirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException | IllegalArgumentException exception) {
            throw new TokenGenerationException("Failed to generate JWT token", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new InvalidTokenException("Invalid or expired JWT token", exception);
        }
    }

    private Instant expirationDate() {
        return Instant.now().plus(expirationHours, ChronoUnit.HOURS);
    }
}
