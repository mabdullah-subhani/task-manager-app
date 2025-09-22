package com.demoproject.taskservice.service;

import com.demoproject.taskservice.dto.TaskRequest;
import com.demoproject.taskservice.dto.TaskResponse;
import com.demoproject.taskservice.entity.Task;
import com.demoproject.taskservice.entity.TaskStatus;
import com.demoproject.taskservice.exception.ResourceNotFoundException;
import com.demoproject.taskservice.mapper.TaskMapper;
import com.demoproject.taskservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Business logic for managing tasks.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;

    public Page<TaskResponse> listTasks(String username, String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks = switch (filter != null ? filter.toLowerCase() : "") {
            case "completed" -> repository.findByUsernameAndStatus(username, TaskStatus.COMPLETED, pageable);
            case "pending" -> repository.findByUsernameAndStatus(username, TaskStatus.PENDING, pageable);
            default -> repository.findByUsername(username, pageable);
        };

        log.info("User {} retrieved {} tasks (filter={})", username, tasks.getTotalElements(), filter);
        return tasks.map(mapper::toResponse);
    }

    public Page<TaskResponse> listAllTasks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional
    public TaskResponse createTask(String username, TaskRequest request) {
        // Prevent backdated tasks
        if (request.getDueDate() != null && request.getDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past.");
        }

        Task task = mapper.toEntity(request);
        task.setUsername(username);
        if (task.getStatus() == null) task.setStatus(TaskStatus.PENDING);

        Task saved = repository.save(task);
        log.info("User {} created task with ID {}", username, saved.getId());
        return mapper.toResponse(saved);
    }


    @Transactional
    public TaskResponse updateTask(String username, Long taskId, TaskRequest request) {
        Task task = repository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        if (!task.getUsername().equals(username))
            throw new IllegalArgumentException("Forbidden: You cannot update this task");

        if (request.getDueDate() != null && request.getDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past.");
        }

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getStatus() != null) task.setStatus(request.getStatus());

        Task updated = repository.save(task);
        log.info("User {} updated task {}", username, taskId);
        return mapper.toResponse(updated);
    }


    @Transactional
    public void deleteTask(String username, Long taskId) {
        Task task = repository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        if (!task.getUsername().equals(username))
            throw new IllegalArgumentException("Forbidden: You cannot delete this task");

        repository.delete(task);
        log.info("User {} deleted task {}", username, taskId);
    }
}

