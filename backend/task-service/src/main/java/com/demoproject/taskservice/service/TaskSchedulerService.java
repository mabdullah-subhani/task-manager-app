package com.demoproject.taskservice.service;

import com.demoproject.taskservice.entity.Task;
import com.demoproject.taskservice.entity.TaskStatus;
import com.demoproject.taskservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSchedulerService {

    private final TaskRepository taskRepository;

    // Runs every 1 minute (60000 ms)
    @Scheduled(fixedRate = 60000)
    public void autoCompleteExpiredTasks() {
        LocalDateTime now = LocalDateTime.now();

        // find all pending tasks where dueDate is before now
        List<Task> expiredTasks = taskRepository.findByStatusAndDueDateBefore(TaskStatus.PENDING, now);

        if (!expiredTasks.isEmpty()) {
            expiredTasks.forEach(task -> task.setStatus(TaskStatus.COMPLETED));
            taskRepository.saveAll(expiredTasks);
            log.info("Auto-completed {} expired tasks", expiredTasks.size());
        }
    }
}

