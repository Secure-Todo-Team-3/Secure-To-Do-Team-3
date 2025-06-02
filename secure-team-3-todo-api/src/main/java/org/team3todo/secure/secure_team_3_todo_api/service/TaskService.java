package org.team3todo.secure.secure_team_3_todo_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Task;
import org.team3todo.secure.secure_team_3_todo_api.repository.TaskRepository;

import java.util.List;


@Service
public class TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskDto> findByTeamId(Long id) {
        List<Task> task = taskRepository.findByTeamId(id);
        return task.stream().map(this::convertToDto).toList();
    }

    public TaskDto convertToDto(Task task) {
        if (task == null) {
            return null;
        }

        TaskDto.TaskDtoBuilder builder = TaskDto.builder()
                .id(task.getId())
                .taskGuid(task.getTaskGuid())
                .name(task.getName())
                .description(task.getDescription())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .dueDate(task.getDueDate());

        if (task.getTeam() != null) {
            builder.teamId(task.getTeam().getId());
            builder.teamName(task.getTeam().getName()); // Assuming Team entity has getName()
        }

        if (task.getAssignedToUser() != null) {
            builder.assignedToUserGuid(task.getAssignedToUser().getUserGuid()); // Assuming User entity has getUserGuid()
            builder.assignedToUsername(task.getAssignedToUser().getUsername()); // Assuming User entity has getUsername()
        }

        if (task.getUserCreator() != null) { // Assuming the field for creator is userCreator
            builder.creatorUserGuid(task.getUserCreator().getUserGuid());
            builder.creatorUsername(task.getUserCreator().getUsername());
        }

        // Logic to get and set currentStatusName and currentStatusId
        // This is more complex and depends on how you manage task status history.
        // Example: if you have a method to get the latest status:
        // TaskStatusHistory latestStatus = taskStatusHistoryService.getLatestStatusForTask(task.getId());
        // if (latestStatus != null && latestStatus.getStatus() != null) {
        //     builder.currentStatusId(latestStatus.getStatus().getId());
        //     builder.currentStatusName(latestStatus.getStatus().getName());
        // }


        return builder.build();
    }
}
