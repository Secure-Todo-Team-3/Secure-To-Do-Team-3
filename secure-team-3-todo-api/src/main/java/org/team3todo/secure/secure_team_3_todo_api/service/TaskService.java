package org.team3todo.secure.secure_team_3_todo_api.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskCreateRequestDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.*;
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

    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService, TeamService teamService,
                       TaskStatusHistoryRepository taskStatusHistoryRepository, TaskStatusRepository taskStatusRepository, TeamMembershipRepository teamMembershipRepository) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.teamService = teamService;
        this.taskStatusHistoryRepository = taskStatusHistoryRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.teamMembershipRepository = teamMembershipRepository;
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
        // 1. Build a Specification for filters that can be applied at the database level.
        Specification<Task> spec = Specification.where(hasAssignedUser(userGuid));

        if (taskName != null && !taskName.isBlank()) {
            spec = spec.and(nameContains(taskName));
        }
        if (teamName != null && !teamName.isBlank()) {
            spec = spec.and(teamNameContains(teamName));
        }

        // 2. Fetch the initial list of tasks from the database.
        List<Task> tasks = taskRepository.findAll(spec);
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. Enrich all found tasks with their current status.
        enrichTasksWithCurrentStatus(tasks);

        // 4. Apply the status filter in-memory, as it's derived from the transient field.
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
        // Use the repository method to find the latest status for this single task
        taskStatusHistoryRepository.findFirstByTaskOrderByChangeTimestampDesc(task)
                .ifPresent(latestStatusHistory -> task.setCurrentStatus(latestStatusHistory.getStatus()));

        return task;
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

    /**
     * Creates a new task and sets its initial status to "To Do".
     *
     * @param taskRequest The DTO containing the new task's data.
     * @param creator     The user entity who is creating the task.
     * @return The newly created and persisted Task entity.
     */
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

        // 2. Build the new Task entity.
        Task newTask = Task.builder()
                .name(taskRequest.getName())
                .description(taskRequest.getDescription())
                .dueDate(taskRequest.getDueDate())
                .team(team)
                .assignedToUser(assignedUser)
                .userCreator(creator)
                .build();

        // 3. Save the new task.
        Task savedTask = taskRepository.save(newTask);

        // 4. Create the initial status history record for the new task.
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
        // 1. Fetch the task or throw an exception if not found.
        Task task = taskRepository.findByTaskGuid(taskGuid)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskGuid));

        // 2. Business Rule: Check if the current user is a member of the task's team.
        boolean isMember = teamMembershipRepository.existsByUserAndTeam(currentUser, task.getTeam());
        if (!isMember) {
            throw new IllegalStateException("Cannot assign task: User '" + currentUser.getUsername() +
                    "' is not a member of team '" + task.getTeam().getName() + "'.");
        }

        // 3. Update the task's assigned user.
        task.setAssignedToUser(currentUser);
        Task updatedTask = taskRepository.save(task);

        // 4. Return the updated task, enriched with its current status.
        return enrichTaskWithCurrentStatus(updatedTask);
    }

    @Transactional
    public Task unassignCurrentUserFromTask(UUID taskGuid, User currentUser) {
        // 1. Fetch the task or throw an exception.
        Task task = taskRepository.findByTaskGuid(taskGuid)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskGuid));

        // 2. Business Rule: Check if the current user is the one assigned to the task.
        if (!task.getAssignedToUser().equals(currentUser)) {
            throw new IllegalStateException("Cannot unassign task: You are not the currently assigned user.");
        }

        // 3. Reassign the task back to its original creator.
        User originalCreator = task.getUserCreator();
        task.setAssignedToUser(originalCreator);
        Task updatedTask = taskRepository.save(task);

        // Optional: You could change the task status back to "To Do" here and log it.

        // 4. Return the updated task, enriched with its current status.
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
