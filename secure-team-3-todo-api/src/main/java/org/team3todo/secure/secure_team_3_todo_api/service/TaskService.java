package org.team3todo.secure.secure_team_3_todo_api.service;

import jakarta.persistence.criteria.Join;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskCreateRequestDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.*;
import org.team3todo.secure.secure_team_3_todo_api.exception.ForbiddenAccessException;
import org.team3todo.secure.secure_team_3_todo_api.exception.ResourceNotFoundException;
import org.team3todo.secure.secure_team_3_todo_api.repository.TaskRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.TaskStatusRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.TeamMembershipRepository;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TeamService teamService;
    private final TaskStatusRepository taskStatusRepository;
    private final TeamMembershipRepository teamMembershipRepository;
    private final TeamMembershipService teamMembershipService;
    private final AuditingService auditingService;


    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService, TeamService teamService,
                       TaskStatusRepository taskStatusRepository, TeamMembershipRepository teamMembershipRepository,
                       TeamMembershipService teamMembershipService, AuditingService auditingService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.teamService = teamService;
        this.taskStatusRepository = taskStatusRepository;
        this.teamMembershipRepository = teamMembershipRepository;
        this.teamMembershipService = teamMembershipService;
        this.auditingService = auditingService;
    }

    public Task findByGuid(UUID guid) {
        return taskRepository.findByTaskGuidWithDetails(guid)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with GUID: " + guid));
    }

    @Transactional(readOnly = true)
    public List<Task> findTasksByTeamId(Long teamId) {
        Team foundTeam = teamService.findById(teamId);
        return taskRepository.findByTeam(foundTeam);
    }

    @Transactional(readOnly = true)
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

    @Transactional
    public Task createTask(TaskCreateRequestDto taskRequest, User creator) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // CHNAGE TO UUID IMPL

        auditingService.setAuditUser(currentUser);

        Team team = teamService.findById(taskRequest.getTeamId());
        User assignedUser = userService.findByUserGuid(taskRequest.getAssignedToUserGuid());

        // Fetch the initial status once.
        TaskStatus initialStatus = taskStatusRepository.findByName("To Do")
                .orElseThrow(() -> new IllegalStateException("Default task status 'To Do' not found in database."));

        Task newTask = Task.builder()
                .name(taskRequest.getName())
                .description(taskRequest.getDescription())
                .dueDate(taskRequest.getDueDate())
                .team(team)
                .assignedToUser(assignedUser)
                .userCreator(creator)
                .taskStatus(initialStatus) // Set status directly on the object.
                .build();

        // Save the task. The database trigger will handle the history log automatically.
        return taskRepository.save(newTask);
    }

    @Transactional
    public Task assignCurrentUserToTask(UUID taskGuid, User currentUser) {
        User currentAUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // CHANGE TO UUID IMPL

        auditingService.setAuditUser(currentAUser);
        Task task = findByGuid(taskGuid);

        boolean isMember = teamMembershipRepository.existsByUserAndTeam(currentUser, task.getTeam());
        if (!isMember) {
            throw new ForbiddenAccessException("Cannot assign task: User is not a member of the task's team.");
        }

        task.setAssignedToUser(currentUser);
        return taskRepository.save(task); // No enrichment needed.
    }

    @Transactional
    public Task unassignCurrentUserFromTask(UUID taskGuid, User currentUser) {
        User currentAUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // CHANGE TO UUID IMPL

        auditingService.setAuditUser(currentAUser);
        Task task = findByGuid(taskGuid);
        if (!currentUser.equals(task.getAssignedToUser())) {
            throw new ForbiddenAccessException("Cannot unassign task: You are not the currently assigned user.");
        }
        task.setAssignedToUser(task.getUserCreator());
        return taskRepository.save(task); // No enrichment needed.
    }

    @Transactional
    public Task assignTaskToTeam(UUID taskGuid, Long teamId, User currentUser) {
        User currentAUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // CHANGE TO UUID IMPL

        auditingService.setAuditUser(currentAUser);

        Task foundTask = taskRepository.findByTaskGuidWithTeam(taskGuid)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with GUID: " + taskGuid));

        Team destinationTeam = teamService.findById(teamId);

        // Authorization checks...
        teamMembershipService.verifyUserIsMember(currentUser.getId(), foundTask.getTeam().getId());
        teamMembershipService.verifyUserIsMember(currentUser.getId(), destinationTeam.getId());

        foundTask.setTeam(destinationTeam);
        foundTask.setAssignedToUser(destinationTeam.getCreatedByUserId()); // Or another default assignee logic

        return taskRepository.save(foundTask); // No enrichment needed.
    }

    // --- Specification Helper Methods ---

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
}