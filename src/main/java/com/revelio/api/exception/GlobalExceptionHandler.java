package com.revelio.api.exception;

import com.revelio.api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.joining("; "));
    log.warn("Validation failed for {}: {}", request.getRequestURI(), message);
    ErrorResponse body =
        ErrorResponse.builder()
            .code("VALIDATION_ERROR")
            .message(message)
            .path(request.getRequestURI())
            .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest request) {
    String message =
        ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .collect(Collectors.joining("; "));
    log.warn("Constraint violation for {}: {}", request.getRequestURI(), message);
    ErrorResponse body =
        ErrorResponse.builder()
            .code("VALIDATION_ERROR")
            .message(message)
            .path(request.getRequestURI())
            .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {
    log.info("Resource not found for {}: {}", request.getRequestURI(), ex.getMessage());
    ErrorResponse body =
        ErrorResponse.builder()
            .code("NOT_FOUND")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(
      BadRequestException ex, HttpServletRequest request) {
    log.warn("Bad request for {}: {}", request.getRequestURI(), ex.getMessage());
    ErrorResponse body =
        ErrorResponse.builder()
            .code("BAD_REQUEST")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
    log.error("Unexpected error for {}", request.getRequestURI(), ex);
    ErrorResponse body =
        ErrorResponse.builder()
            .code("INTERNAL_ERROR")
            .message("An unexpected error occurred")
            .path(request.getRequestURI())
            .build();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
