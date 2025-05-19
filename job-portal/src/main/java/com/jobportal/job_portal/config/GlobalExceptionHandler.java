package com.jobportal.job_portal.config;

import com.jobportal.job_portal.common.ApiException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        return buildErrorResponse(ex.getMessage(), ex.getStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        return buildErrorResponse("Accès refusé : vous n'avez pas les permissions nécessaires", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        return buildErrorResponse("Authentification échouée : " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder message = new StringBuilder("Erreurs de validation : ");
        ex.getBindingResult().getFieldErrors().forEach(
                error -> message.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; "));
        return buildErrorResponse(message.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        StringBuilder message = new StringBuilder("Erreurs de validation : ");
        ex.getConstraintViolations().forEach(violation -> message.append(violation.getPropertyPath()).append(" - ")
                .append(violation.getMessage()).append("; "));
        return buildErrorResponse(message.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildErrorResponse("Une erreur inattendue s'est produite : " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        response.put("data", null);
        return new ResponseEntity<>(response, status);
    }
}