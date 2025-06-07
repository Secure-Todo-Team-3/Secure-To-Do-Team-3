package org.team3todo.secure.secure_team_3_todo_api.mapper;

import org.springframework.stereotype.Component;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.*;
import org.team3todo.secure.secure_team_3_todo_api.repository.TaskStatusHistoryRepository;
// If you implement status logic within the mapper that needs services:
// import org.team3todo.secure.secure_team_3_todo_api.service.TaskStatusHistoryService;
// import org.team3todo.secure.secure_team_3_todo_api.entity.TaskStatusHistory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TaskMapper {


     private final TaskStatusHistoryRepository taskStatusHistoryRepository;

     public TaskMapper(TaskStatusHistoryRepository taskStatusHistoryRepository) {
     this.taskStatusHistoryRepository = taskStatusHistoryRepository;
     }

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

        Optional<TaskStatusHistory> latestStatusOpt = taskStatusHistoryRepository
                .findFirstByTaskOrderByChangeTimestampDesc(task);

        if (latestStatusOpt.isPresent()) {
            TaskStatus currentStatus = latestStatusOpt.get().getStatus();
            if (currentStatus != null) {
                builder.currentStatusId(currentStatus.getId());
                builder.currentStatusName(currentStatus.getName());
            }
        }

        return builder.build();
    }

    public List<TaskDto> convertToDtoList(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return Collections.emptyList();
        }
        return tasks.stream()
                .map(this::convertToDto) // Use 'this' for instance method
                .collect(Collectors.toList());
    }

    // You can add a convertToEntity method here if needed for task creation/updates
    /*
     * public Task convertToEntity(TaskDto taskDto, User creator, User assignee, Team
     * team) {
     * if (taskDto == null) {
     * return null;
     * }
     * return Task.builder()
     * .name(taskDto.getName())
     * .description(taskDto.getDescription())
     * .dueDate(taskDto.getDueDate())
     * .userCreator(creator) // these would be fetched/validated in the service
     * .assignedToUser(assignee) // before calling the mapper
     * .team(team)
     * // taskGuid, createdAt, updatedAt are usually handled by JPA/DB
     * .build();
     * }
     */
}