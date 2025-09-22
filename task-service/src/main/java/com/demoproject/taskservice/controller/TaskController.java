package com.demoproject.taskservice.controller;

import com.demoproject.taskservice.dto.TaskRequest;
import com.demoproject.taskservice.dto.TaskResponse;
import com.demoproject.taskservice.payload.ApiResponsePayload;
import com.demoproject.taskservice.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "List user tasks", description = "Retrieve paginated list of tasks for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    @GetMapping
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponsePayload<Page<TaskResponse>>> listTasks(
            Authentication auth,
            @RequestParam(required = false) String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        String username = auth.getName();
        Page<TaskResponse> tasks = taskService.listTasks(username, filter, page, size);
        return ResponseEntity.ok(new ApiResponsePayload<>(true, "Tasks retrieved", tasks));
    }

    @Operation(summary = "Create a new task", description = "Create a new task for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Task created successfully")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponsePayload<TaskResponse>> createTask(
            Authentication auth,
            @Valid @RequestBody TaskRequest request) {

        String username = auth.getName();
        TaskResponse created = taskService.createTask(username, request);
        return ResponseEntity.ok(new ApiResponsePayload<>(true, "Task created", created));
    }

    @Operation(summary = "Update a task", description = "Update an existing task by ID for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Task updated successfully")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponsePayload<TaskResponse>> updateTask(
            Authentication auth,
            @PathVariable Long id,
            @RequestBody TaskRequest request) {

        String username = auth.getName();
        TaskResponse updated = taskService.updateTask(username, id, request);
        return ResponseEntity.ok(new ApiResponsePayload<>(true, "Task updated", updated));
    }

    @Operation(summary = "Delete a task", description = "Delete an existing task by ID for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Task deleted successfully")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponsePayload<Void>> deleteTask(
            Authentication auth,
            @PathVariable Long id) {

        String username = auth.getName();
        taskService.deleteTask(username, id);
        return ResponseEntity.ok(new ApiResponsePayload<>(true, "Task deleted", null));
    }

    @Operation(summary = "List all tasks (Admin only)", description = "Retrieve all tasks across users (Admin role required)")
    @ApiResponse(responseCode = "200", description = "All tasks retrieved successfully")
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponsePayload<Page<TaskResponse>>> listAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<TaskResponse> tasks = taskService.listAllTasks(page, size);
        return ResponseEntity.ok(new ApiResponsePayload<>(true, "All tasks retrieved", tasks));
    }
}
