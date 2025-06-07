package org.team3todo.secure.secure_team_3_todo_api.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.*;
import org.team3todo.secure.secure_team_3_todo_api.exception.ResourceNotFoundException;
import org.team3todo.secure.secure_team_3_todo_api.repository.TaskRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.TaskStatusHistoryRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TeamService teamService;
    private final TaskStatusHistoryRepository taskStatusHistoryRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService, TeamService teamService,
                       TaskStatusHistoryRepository taskStatusHistoryRepository) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.teamService = teamService;
        this.taskStatusHistoryRepository = taskStatusHistoryRepository;
    }

    /**
     * Finds all tasks for a team and enriches them with their current status.
     *
     * @param teamId The ID of the team.
     * @return A list of enriched Task entities.
     */
    @Transactional()
    public List<Task> findEnrichedTasksByTeamId(Long teamId) {
        Team foundTeam = teamService.findById(teamId);
        if (foundTeam == null) {
            throw new ResourceNotFoundException("Cannot find tasks for a non-existent team with ID: " + teamId);
        }

        List<Task> tasks = taskRepository.findByTeam(foundTeam);
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        enrichTasksWithCurrentStatus(tasks);
        return tasks;
    }

    /**
     * Finds all tasks for a user and enriches them with their current status.
     * This is the method your controller should call.
     *
     * @param userGuid The GUID of the user.
     * @return A list of enriched Task entities with the currentStatus field populated.
     */
    @Transactional()
    public List<Task> findEnrichedTasksByAssignedUserGuid(UUID userGuid) {
        List<Task> tasks = this.findByAssignedUserGuid(userGuid);
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }
        enrichTasksWithCurrentStatus(tasks);
        return tasks;
    }

    /**
     * Helper method to efficiently fetch and set the current status on a list of tasks.
     *
     * @param tasks The list of tasks to enrich.
     */
    private void enrichTasksWithCurrentStatus(List<Task> tasks) {
        List<Long> taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());

        List<TaskStatusHistory> latestStatuses = taskStatusHistoryRepository.findLatestStatusForTasks(taskIds);

        Map<Long, TaskStatus> statusMap = latestStatuses.stream()
                .collect(Collectors.toMap(history -> history.getTask().getId(), TaskStatusHistory::getStatus));

        tasks.forEach(task -> task.setCurrentStatus(statusMap.get(task.getId())));
    }

    /**
     * Private helper method to find tasks assigned to a user. Throws an exception if the user is not found.
     * This method returns the raw entity list without status enrichment.
     */
    private List<Task> findByAssignedUserGuid(UUID guid) {
        User foundUser = userService.findByUserGuid(guid); // Assumes this returns User or null
        if (foundUser == null) {
            throw new ResourceNotFoundException("User not found with GUID: " + guid);
        }
        return taskRepository.findByAssignedToUser(foundUser);
    }
}
