package org.team3todo.secure.secure_team_3_todo_api.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskCreateRequestDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskDto;
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
    public Task createTask(TaskCreateRequestDto taskRequest, User creator) {

        Team team = teamService.findById(taskRequest.getTeamId());
        if(team == null){
            throw new ResourceNotFoundException("Cannot create task: Team not found with ID: " + taskRequest.getTeamId());
        }

        User assignedUser = userService.findByUserGuid(taskRequest.getAssignedToUserGuid());

        if(assignedUser == null){
            throw new ResourceNotFoundException("Cannot create task: Assigned user not found with GUID: " + taskRequest.getAssignedToUserGuid());
        }

        TaskStatus initialStatus = taskStatusRepository.findByName("To Do")
                .orElseThrow(() -> new IllegalStateException("Default task status 'To Do' not found in database."));

        Task newTask = Task.builder()
                .name(taskRequest.getName())
                .description(taskRequest.getDescription())
                .dueDate(taskRequest.getDueDate())
                .team(team)
                .assignedToUser(assignedUser)
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

}
