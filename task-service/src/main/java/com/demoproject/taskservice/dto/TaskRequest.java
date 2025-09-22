package com.demoproject.taskservice.dto;

import com.demoproject.taskservice.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private TaskStatus status; // optional, defaults to PENDING

    private LocalDateTime dueDate;
}

