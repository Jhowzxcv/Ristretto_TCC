package com.Ristretto.API.infra.exception;

import com.Ristretto.API.infra.exception.Domain.ResourceNotFoundException;
import com.Ristretto.API.infra.exception.Token.TokenException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // -------------------- 404 --------------------
    @ExceptionHandler({EntityNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<?> handleNotFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recurso não encontrado");
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<?> handleEmptyResult(EmptyResultDataAccessException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recurso não encontrado");
    }

    // -------------------- 400 --------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        var errors = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(errors.stream()
                .map(ValidationErrorData::new)
                .toList());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidJson(HttpMessageNotReadableException ex) {
        String message = "Requisição inválida: " + ex.getMostSpecificCause().getMessage();
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format(
                "Parâmetro '%s' inválido: valor '%s' não pôde ser convertido para %s",
                ex.getName(), ex.getValue(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "tipo esperado"
        );
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler({ValidationException.class, ConstraintViolationException.class,
            IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<?> handleBusinessValidation(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParams(MissingServletRequestParameterException ex) {
        String message = String.format("Parâmetro obrigatório '%s' do tipo '%s' não foi fornecido",
                ex.getParameterName(), ex.getParameterType());
        return ResponseEntity.badRequest().body(message);
    }

    // -------------------- 401 --------------------
    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class, TokenException.class})
    public ResponseEntity<?> handleUnauthorized(Exception ex) {
        String message = switch (ex.getClass().getSimpleName()) {
            case "BadCredentialsException" -> "Credenciais inválidas";
            case "AuthenticationException" -> "Falha na autenticação";
            case "TokenException" -> ex.getMessage();
            default -> "Não autorizado";
        };
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }

    // -------------------- 403 --------------------
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleForbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado");
    }

    // -------------------- 500 --------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleInternalErrors(Exception ex) {
        log.error("Erro interno não tratado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno: " + ex.getLocalizedMessage());
    }

    // -------------------- Aux --------------------
    private record ValidationErrorData(String field, String message) {
        public ValidationErrorData(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}