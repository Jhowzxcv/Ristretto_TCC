package com.Ristretto.API.infra.exception.Token;

public class TokenGenerationException extends TokenException {

    public TokenGenerationException() {
        super("Erro ao gerar token.");
    }

    public TokenGenerationException(String message) {
        super(message);
    }

    public TokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
