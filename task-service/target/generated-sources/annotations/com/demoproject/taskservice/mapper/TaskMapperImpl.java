package com.demoproject.taskservice.mapper;

import com.demoproject.taskservice.dto.TaskRequest;
import com.demoproject.taskservice.dto.TaskResponse;
import com.demoproject.taskservice.entity.Task;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-22T15:37:30+0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class TaskMapperImpl implements TaskMapper {

    @Override
    public TaskResponse toResponse(Task task) {
        if ( task == null ) {
            return null;
        }

        TaskResponse.TaskResponseBuilder taskResponse = TaskResponse.builder();

        taskResponse.id( task.getId() );
        taskResponse.title( task.getTitle() );
        taskResponse.description( task.getDescription() );
        taskResponse.status( task.getStatus() );
        taskResponse.username( task.getUsername() );
        taskResponse.dueDate( task.getDueDate() );
        taskResponse.createdAt( task.getCreatedAt() );
        taskResponse.updatedAt( task.getUpdatedAt() );

        return taskResponse.build();
    }

    @Override
    public Task toEntity(TaskRequest request) {
        if ( request == null ) {
            return null;
        }

        Task.TaskBuilder task = Task.builder();

        task.title( request.getTitle() );
        task.description( request.getDescription() );
        task.status( request.getStatus() );
        task.dueDate( request.getDueDate() );

        return task.build();
    }
}
