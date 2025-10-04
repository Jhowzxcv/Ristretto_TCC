package com.Ristretto.API.infra.exception.Token;

public class InvalidTokenException extends TokenException {

    public InvalidTokenException() {
        super("Token inválido.");
    }

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
