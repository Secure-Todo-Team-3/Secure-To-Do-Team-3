package org.team3todo.secure.secure_team_3_todo_api.service;

import jakarta.persistence.criteria.Join;
import jakarta.transaction.Transactional;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskCreateRequestDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskUpdateRequestDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.*;
import org.team3todo.secure.secure_team_3_todo_api.exception.DuplicateResourceException;
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
    private final AuditingService auditingService;
    private final PolicyFactory sanitizerPolicy;


    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService, TeamService teamService,
                       TaskStatusRepository taskStatusRepository, TeamMembershipRepository teamMembershipRepository,
                       TeamMembershipService teamMembershipService, AuditingService auditingService, TaskStatusHistoryRepository taskStatusHistoryRepository,
                       PolicyFactory sanitizerPolicy) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.teamService = teamService;
        this.taskStatusHistoryRepository = taskStatusHistoryRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.teamMembershipRepository = teamMembershipRepository;
        this.teamMembershipService = teamMembershipService;
        this.auditingService = auditingService;
        this.sanitizerPolicy = sanitizerPolicy;
    }

    public Task findByGuid(UUID guid) {
        return taskRepository.findByTaskGuidWithDetails(guid)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with GUID: " + guid));
    }

    public List<Task> findTasksByTeamId(Long teamId) {
        Team foundTeam = teamService.findById(teamId);
        return taskRepository.findByTeam(foundTeam);
    }

    @Transactional()
    public List<Task> findTasksByAssignedUser(UUID userGuid, String taskName, String statusName, String teamName) {
        // Build a dynamic query using Specifications for database-level filtering.
        Specification<Task> spec = Specification.where(hasAssignedUser(userGuid));

        if (taskName != null && !taskName.isBlank()) {
            spec = spec.and(nameContains(taskName));
        }
        if (teamName != null && !teamName.isBlank()) {
            spec = spec.and(teamNameContains(teamName));
        }
        if (statusName != null && !statusName.isBlank()) {
            spec = spec.and(hasStatus(statusName));
        }

        return taskRepository.findAll(spec);
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
        auditingService.setAuditUser(creator);
        String safeName = sanitizerPolicy.sanitize(taskRequest.getName());
        String safeDescription = sanitizerPolicy.sanitize(taskRequest.getDescription());
        Team team = teamService.findById(taskRequest.getTeamId());
        if (team == null) {
            throw new ResourceNotFoundException("Cannot create task: Team not found with ID: " + taskRequest.getTeamId());
        }

        TaskStatus initialStatus = taskStatusRepository.findByName("To Do")
                .orElseThrow(() -> new IllegalStateException("Default task status 'To Do' not found in database."));

        Task newTask = Task.builder()
                .name(safeName)
                .description(safeDescription)
                .dueDate(taskRequest.getDueDate())
                .team(team)
                .assignedToUser(creator)
                .userCreator(creator)
                .taskStatus(initialStatus) // Set status directly on the object.
                .build();

        // Save the task. The database trigger will handle the history log automatically.
        return taskRepository.save(newTask);
    }

    @Transactional
    public Task assignCurrentUserToTask(UUID taskGuid, UUID currentUserUUID) {
        User currentUser = userService.findByUserGuid(currentUserUUID);
        auditingService.setAuditUser(currentUser);
        Task task = findByGuid(taskGuid);

        boolean isMember = teamMembershipRepository.existsByUserAndTeam(currentUser, task.getTeam());
        if (!isMember) {
            throw new ForbiddenAccessException("Cannot assign task: User is not a member of the task's team.");
        }

        task.setAssignedToUser(currentUser);
        return taskRepository.save(task);
    }

    @Transactional
    public Task unassignCurrentUserFromTask(UUID taskGuid, UUID currentUserGuid) {
        User currentUser = userService.findByUserGuid(currentUserGuid);
        auditingService.setAuditUser(currentUser);

        Task task = findByGuid(taskGuid);
        if (!currentUser.equals(task.getAssignedToUser())) {
            throw new ForbiddenAccessException("Cannot unassign task: You are not the currently assigned user.");
        }
        task.setAssignedToUser(task.getUserCreator());
        return taskRepository.save(task); // No enrichment needed.
    }

    @Transactional
    public Task assignTaskToTeam(UUID taskGuid, Long teamId, UUID currentUserGuid) {
        User currentUser = userService.findByUserGuid(currentUserGuid);
        auditingService.setAuditUser(currentUser);

        Task foundTask = taskRepository.findByTaskGuidWithTeam(taskGuid)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with GUID: " + taskGuid));

        Team destinationTeam = teamService.findById(teamId);

        teamMembershipService.verifyUserIsMember(currentUser.getId(), foundTask.getTeam().getId());
        teamMembershipService.verifyUserIsMember(currentUser.getId(), destinationTeam.getId());

        foundTask.setTeam(destinationTeam);
        foundTask.setAssignedToUser(destinationTeam.getCreatedByUserId());

        return taskRepository.save(foundTask);
    }

    private Specification<Task> hasAssignedUser(UUID userGuid) {
        return (root, query, cb) -> cb.equal(root.get("assignedToUser").get("userGuid"), userGuid);
    }

    private Specification<Task> nameContains(String taskName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + taskName.toLowerCase() + "%");
    }

    private Specification<Task> teamNameContains(String teamName) {
        return (root, query, criteriaBuilder) -> {
            Join<Task, Team> teamJoin = root.join("team");
            return criteriaBuilder.like(criteriaBuilder.lower(teamJoin.get("name")), "%" + teamName.toLowerCase() + "%");
        };
    }

    private Specification<Task> hasStatus(String statusName) {

        return (root, query, criteriaBuilder) -> {
            Join<Task, TaskStatus> statusJoin = root.join("taskStatus");
            return criteriaBuilder.like(criteriaBuilder.lower(statusJoin.get("name")), "%" + statusName.toLowerCase() + "%");
        };
    }

    public Task updateTask(UUID taskGuid, TaskUpdateRequestDto taskUpdateRequest, UUID updaterGuid) {
        Task taskToUpdate = taskRepository.findByTaskGuidWithDetails(taskGuid)
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
            taskToUpdate.setName(sanitizerPolicy.sanitize(taskUpdateRequest.getName()));
        }
        if (taskUpdateRequest.getDescription() != null) {
            taskToUpdate.setDescription(sanitizerPolicy.sanitize(taskUpdateRequest.getDescription()));
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

        return taskRepository.save(taskToUpdate);
    }

}
