package org.team3todo.secure.secure_team_3_todo_api.mapper;

import org.springframework.stereotype.Component;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Task;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
// If you implement status logic within the mapper that needs services:
// import org.team3todo.secure.secure_team_3_todo_api.service.TaskStatusHistoryService;
// import org.team3todo.secure.secure_team_3_todo_api.entity.TaskStatusHistory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskMapper {

    // If you need to fetch additional data (like current status) from a service:
    // private final TaskStatusHistoryService taskStatusHistoryService;
    //
    // public TaskMapper(TaskStatusHistoryService taskStatusHistoryService) {
    // this.taskStatusHistoryService = taskStatusHistoryService;
    // }
    // For now, keeping it simple and assuming status info comes from Task entity or
    // is set on DTO elsewhere.

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

        // Regarding currentStatusName and currentStatusId in TaskDto:
        // The mapping logic you provided does not populate these.
        // If your Task entity has direct fields for currentStatusName/Id (e.g.,
        // transient or denormalized),
        // you would map them here:
        // builder.currentStatusId(task.getCurrentStatusIdOnEntity());
        // builder.currentStatusName(task.getCurrentStatusNameOnEntity());

        // If you were to fetch it via a service injected into this mapper (more
        // complex mapper):
        /*
         * if (taskStatusHistoryService != null) {
         * TaskStatusHistory latestStatus =
         * taskStatusHistoryService.getLatestStatusForTask(task.getId());
         * if (latestStatus != null && latestStatus.getStatus() != null) {
         * builder.currentStatusId(latestStatus.getStatus().getId());
         * builder.currentStatusName(latestStatus.getStatus().getName());
         * }
         * }
         */

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