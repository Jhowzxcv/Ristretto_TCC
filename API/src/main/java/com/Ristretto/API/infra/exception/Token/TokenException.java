package com.Ristretto.API.infra.exception.Token;

public class TokenException extends RuntimeException {

    public TokenException() {
        super("Erro relacionado ao token.");
    }

    public TokenException(String message) {
        super(message);
    }

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenException(Throwable cause) {
        super(cause);
    }
}
