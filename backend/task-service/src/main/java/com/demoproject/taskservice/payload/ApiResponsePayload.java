package com.demoproject.taskservice.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponsePayload<T> {
    private boolean success;
    private String message;
    private T data;
}
