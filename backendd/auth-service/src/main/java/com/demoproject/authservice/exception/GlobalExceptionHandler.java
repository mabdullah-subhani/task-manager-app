package com.demoproject.authservice.exception;

import com.demoproject.authservice.payload.ApiResponse;
import jakarta.ws.rs.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(404).body(
                ApiResponse.<String>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build()
        );
    }

    // Handle bad requests (duplicate username/email, invalid login, etc.)
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<String>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.badRequest().body(
                ApiResponse.<String>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build()
        );
    }

    // Handle validation errors from @Valid DTOs
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(
                ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation failed")
                        .data(errors)
                        .build()
        );
    }

    // Catch all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleAllExceptions(Exception ex) {
        return ResponseEntity.status(500).body(
                ApiResponse.<String>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build()
        );
    }
}
