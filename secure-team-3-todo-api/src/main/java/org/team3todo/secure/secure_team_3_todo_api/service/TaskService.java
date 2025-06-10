package org.team3todo.secure.secure_team_3_todo_api.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskCreateRequestDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskUpdateRequestDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.*;
import org.team3todo.secure.secure_team_3_todo_api.exception.ForbiddenAccessException;
import org.team3todo.secure.secure_team_3_todo_api.exception.ResourceNotFoundException;
import org.team3todo.secure.secure_team_3_todo_api.repository.TaskRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.TaskStatusHistoryRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.TaskStatusRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.TeamMembershipRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TeamService teamService;
    private final TaskStatusHistoryRepository taskStatusHistoryRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final TeamMembershipRepository teamMembershipRepository;
    private final TeamMembershipService teamMembershipService;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService, TeamService teamService,
                       TaskStatusHistoryRepository taskStatusHistoryRepository, TaskStatusRepository taskStatusRepository, TeamMembershipRepository teamMembershipRepository, TeamMembershipService teamMembershipService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.teamService = teamService;
        this.taskStatusHistoryRepository = taskStatusHistoryRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.teamMembershipRepository = teamMembershipRepository;
        this.teamMembershipService = teamMembershipService;
    }

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
     * Finds tasks for a user with optional filters for task name, team name, and status.
     *
     * @param userGuid The GUID of the user to find tasks for.
     * @param taskName (Optional) Filter for tasks containing this name.
     * @param statusName (Optional) Filter for tasks with this current status name.
     * @param teamName (Optional) Filter for tasks belonging to a team containing this name.
     * @return A list of enriched Task entities that match the criteria.
     */
    @Transactional()
    public List<Task> findEnrichedTasksByAssignedUser(UUID userGuid, String taskName, String statusName, String teamName) {
        Specification<Task> spec = Specification.where(hasAssignedUser(userGuid));
        if (taskName != null && !taskName.isBlank()) {
            spec = spec.and(nameContains(taskName));
        }
        if (teamName != null && !teamName.isBlank()) {
            spec = spec.and(teamNameContains(teamName));
        }
        List<Task> tasks = taskRepository.findAll(spec);
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }
        enrichTasksWithCurrentStatus(tasks);
        if (statusName != null && !statusName.isBlank()) {
            return tasks.stream()
                    .filter(task -> task.getCurrentStatus() != null &&
                            statusName.equalsIgnoreCase(task.getCurrentStatus().getName()))
                    .collect(Collectors.toList());
        }

        return tasks;
    }

    /**
     * Helper method to efficiently fetch and set the current status on a list of tasks.
     *
     * @param tasks The list of tasks to enrich.
     */
    public void enrichTasksWithCurrentStatus(List<Task> tasks) {
        List<Long> taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());

        List<TaskStatusHistory> latestStatuses = taskStatusHistoryRepository.findLatestStatusForTasks(taskIds);

        Map<Long, TaskStatus> statusMap = latestStatuses.stream()
                .collect(Collectors.toMap(history -> history.getTask().getId(), TaskStatusHistory::getStatus));

        tasks.forEach(task -> task.setCurrentStatus(statusMap.get(task.getId())));
    }

    public Task enrichTaskWithCurrentStatus(Task task) {
        if (task == null) {
            return null;
        }
        taskStatusHistoryRepository.findFirstByTaskOrderByChangeTimestampDesc(task)
                .ifPresent(latestStatusHistory -> task.setCurrentStatus(latestStatusHistory.getStatus()));

        return task;
    }

    /**
     * Private helper method to find tasks assigned to a user. Throws an exception if the user is not found.
     * This method returns the raw entity list without status enrichment.
     */
    private List<Task> findByAssignedUserGuid(UUID guid) {
        User foundUser = userService.findByUserGuid(guid);
        if (foundUser == null) {
            throw new ResourceNotFoundException("User not found with GUID: " + guid);
        }
        return taskRepository.findByAssignedToUser(foundUser);
    }

    @Transactional
    public Task createTask(TaskCreateRequestDto taskRequest, UUID creatorGuid) {
        User creator = userService.findByUserGuid(creatorGuid);
        Team team = teamService.findById(taskRequest.getTeamId());
        if(team == null){
            throw new ResourceNotFoundException("Cannot create task: Team not found with ID: " + taskRequest.getTeamId());
        }
        
        TaskStatus initialStatus = taskStatusRepository.findByName("To Do")
                .orElseThrow(() -> new IllegalStateException("Default task status 'To Do' not found in database."));

        Task newTask = Task.builder()
                .name(taskRequest.getName())
                .description(taskRequest.getDescription())
                .dueDate(taskRequest.getDueDate())
                .team(team)
                .assignedToUser(creator)
                .userCreator(creator)
                .build();

        Task savedTask = taskRepository.save(newTask);

        TaskStatusHistory initialHistory = TaskStatusHistory.builder()
                .task(savedTask)
                .status(initialStatus)
                .changedByUser(creator)
                .build();
        taskStatusHistoryRepository.save(initialHistory);

        return savedTask;
    }

    @Transactional
    public Task assignCurrentUserToTask(UUID taskGuid, User currentUser) {
        Task task = taskRepository.findByTaskGuid(taskGuid)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskGuid));

        boolean isMember = teamMembershipRepository.existsByUserAndTeam(currentUser, task.getTeam());
        if (!isMember) {
            throw new IllegalStateException("Cannot assign task: User '" + currentUser.getUsername() +
                    "' is not a member of team '" + task.getTeam().getName() + "'.");
        }

        task.setAssignedToUser(currentUser);
        Task updatedTask = taskRepository.save(task);

        return enrichTaskWithCurrentStatus(updatedTask);
    }

    @Transactional
    public Task unassignCurrentUserFromTask(UUID taskGuid, User currentUser) {
        Task task = taskRepository.findByTaskGuid(taskGuid)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskGuid));
        if (!task.getAssignedToUser().equals(currentUser)) {
            throw new IllegalStateException("Cannot unassign task: You are not the currently assigned user.");
        }
        User originalCreator = task.getUserCreator();
        task.setAssignedToUser(originalCreator);
        Task updatedTask = taskRepository.save(task);
        return enrichTaskWithCurrentStatus(updatedTask);
    }

    @Transactional
    public Task assignTaskToTeam(UUID taskGuid, Long teamId, User currentUser) {
        Task foundTask = taskRepository.findByTaskGuidWithTeam(taskGuid)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with GUID: " + taskGuid));

        Team foundDestinationTeam = teamService.findById(teamId);
        if (foundDestinationTeam == null) {
            throw new ResourceNotFoundException("Team with ID " + teamId + " does not exist.");
        }

        Team currentTeam = foundTask.getTeam();
        if (teamService.getUsersInATeam(currentTeam.getId()).stream().noneMatch(user -> user.equals(currentUser))) {
            throw new ForbiddenAccessException("You can't reassign this task if you aren't in its currently assigned team.");
        }

        boolean userInDestinationTeam = teamMembershipService.isUserInTeam(currentUser, foundDestinationTeam);
        if (!userInDestinationTeam) {
            throw new ForbiddenAccessException("You cannot assign a task to a team you are not a member of.");
        }
        foundTask.setTeam(foundDestinationTeam);
        User destinationTeamsCreator = foundDestinationTeam.getCreatedByUserId();
        foundTask.setAssignedToUser(destinationTeamsCreator);
        Task updatedTask = taskRepository.save(foundTask);

        return enrichTaskWithCurrentStatus(updatedTask);
    }

    private Specification<Task> hasAssignedUser(UUID userGuid) {
        return (root, query, cb) -> cb.equal(root.get("assignedToUser").get("userGuid"), userGuid);
    }

    private Specification<Task> nameContains(String taskName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + taskName.toLowerCase() + "%");
    }

    private Specification<Task> teamNameContains(String teamName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("team").get("name")), "%" + teamName.toLowerCase() + "%");
    }

    public Task findByTaskGuid(UUID taskGuid) { // needs protection
        return taskRepository.findByTaskGuid(taskGuid)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with GUID: " + taskGuid));
    }

    public Task updateTask(UUID taskGuid, TaskUpdateRequestDto taskUpdateRequest, UUID updaterGuid) {
        Task taskToUpdate = taskRepository.findByTaskGuid(taskGuid)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with GUID: " + taskGuid));

        User updater = userService.findByUserGuid(updaterGuid);
        if (updater == null) {
            throw new ResourceNotFoundException("Updater user not found with GUID: " + updaterGuid);
        }

        Optional<TeamMembership> membership = teamMembershipRepository.findByUserAndTeam(updater, taskToUpdate.getTeam());

        if (membership.isEmpty()) {
            throw new ForbiddenAccessException("You do not have permission to update this task in team: " + taskToUpdate.getTeam().getName());
        }

        if (taskUpdateRequest.getName() != null) {
            taskToUpdate.setName(taskUpdateRequest.getName());
        }
        if (taskUpdateRequest.getDescription() != null) {
            taskToUpdate.setDescription(taskUpdateRequest.getDescription());
        }
        if (taskUpdateRequest.getDueDate() != null) {
            taskToUpdate.setDueDate(taskUpdateRequest.getDueDate());
        }
        if (taskUpdateRequest.getCurrentStatusId() != null) {
            TaskStatus newStatus = taskStatusRepository.findById(taskUpdateRequest.getCurrentStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task status not found with ID: " + taskUpdateRequest.getCurrentStatusId()));
            TaskStatusHistory newHistory = TaskStatusHistory.builder()
                    .task(taskToUpdate)
                    .status(newStatus)
                    .changedByUser(updater)
                    .build();
            taskStatusHistoryRepository.save(newHistory);
        }

        Task updatedTask = taskRepository.save(taskToUpdate);
        return enrichTaskWithCurrentStatus(updatedTask);
    }

}
