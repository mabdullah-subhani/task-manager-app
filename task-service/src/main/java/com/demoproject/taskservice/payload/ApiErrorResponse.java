package com.demoproject.taskservice.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API Error Response.
 * Used by GlobalExceptionHandler to return consistent error messages.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object details;

    // ✅ Static factory method
    public static ApiErrorResponse of(int status, String error, String message, Object details) {
        return new ApiErrorResponse(LocalDateTime.now(), status, error, message, details);
    }
}

