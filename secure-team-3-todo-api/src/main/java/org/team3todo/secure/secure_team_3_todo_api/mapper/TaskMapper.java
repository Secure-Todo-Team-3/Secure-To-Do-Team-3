package org.team3todo.secure.secure_team_3_todo_api.mapper;

import org.springframework.stereotype.Component;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Task;
import org.team3todo.secure.secure_team_3_todo_api.entity.TaskStatus;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskMapper {

    public TaskMapper() { }

    public TaskDto convertToDto(Task task) {
        if (task == null) {
            return null;
        }

        TaskDto.TaskDtoBuilder builder = TaskDto.builder()
                .taskGuid(task.getTaskGuid())
                .name(task.getName())
                .description(task.getDescription())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .dueDate(task.getDueDate());

        Team team = task.getTeam();
        if (team != null) {
            builder.teamId(team.getId());
            builder.teamName(team.getName());
        }

        User assignedToUser = task.getAssignedToUser();
        if (assignedToUser != null) {
            builder.assignedToUserGuid(assignedToUser.getUserGuid());
            builder.assignedToUsername(assignedToUser.getUsername());
        }

        User userCreator = task.getUserCreator();
        if (userCreator != null) {
            builder.creatorUserGuid(userCreator.getUserGuid());
            builder.creatorUsername(userCreator.getUsername());
        }

        TaskStatus currentStatus = task.getTaskStatus();
        if (currentStatus != null) {
            builder.currentStatusId(currentStatus.getId().longValue());
            builder.currentStatusName(currentStatus.getName());
        }

        return builder.build();
    }

    public List<TaskDto> convertToDtoList(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return Collections.emptyList();
        }
        return tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}