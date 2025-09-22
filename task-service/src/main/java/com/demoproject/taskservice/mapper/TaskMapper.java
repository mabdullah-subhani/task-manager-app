package com.demoproject.taskservice.mapper;


import com.demoproject.taskservice.dto.TaskRequest;
import com.demoproject.taskservice.dto.TaskResponse;
import com.demoproject.taskservice.entity.Task;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskResponse toResponse(Task task);

    // Map TaskRequest -> Task
    @Mapping(target = "id", ignore = true) // id is generated
    Task toEntity(TaskRequest request);
}