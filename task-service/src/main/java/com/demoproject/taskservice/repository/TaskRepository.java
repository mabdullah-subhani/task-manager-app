package com.demoproject.taskservice.repository;


import com.demoproject.taskservice.entity.Task;
import com.demoproject.taskservice.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // List all tasks for a specific user
    Page<Task> findByUsername(String username, Pageable pageable);

    // Filter by status
    Page<Task> findByUsernameAndStatus(String username, TaskStatus status, Pageable pageable);

    // Overdue tasks
    Page<Task> findByUsernameAndDueDateBeforeAndStatus(String username, LocalDateTime date, TaskStatus status, Pageable pageable);

    // Scheduler tasks
    List<Task> findByStatusAndDueDateBefore(TaskStatus status, LocalDateTime dueDate);

}

