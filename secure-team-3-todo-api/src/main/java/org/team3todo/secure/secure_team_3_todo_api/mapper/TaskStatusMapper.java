package org.team3todo.secure.secure_team_3_todo_api.mapper;

import org.springframework.stereotype.Component;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskStatusDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.TaskStatus;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskStatusMapper {

    /**
     * Converts a TaskStatus entity to a TaskStatusDto.
     *
     * @param taskStatus The entity to convert.
     * @return The converted DTO, or null if the input is null.
     */
    public TaskStatusDto convertToDto(TaskStatus taskStatus) {
        if (taskStatus == null) {
            return null;
        }

        return TaskStatusDto.builder()
                .id(taskStatus.getId())
                .name(taskStatus.getName())
                .description(taskStatus.getDescription())
                .build();
    }

    /**
     * Converts a list of TaskStatus entities to a list of TaskStatusDto objects.
     *
     * @param taskStatuses The list of entities to convert.
     * @return A list of DTOs, or an empty list if the input is null or empty.
     */
    public List<TaskStatusDto> convertToDtoList(List<TaskStatus> taskStatuses) {
        if (taskStatuses == null || taskStatuses.isEmpty()) {
            return Collections.emptyList();
        }
        return taskStatuses.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}