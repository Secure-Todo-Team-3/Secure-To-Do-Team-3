package org.team3todo.secure.secure_team_3_todo_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskCreateRequestDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Task;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.mapper.TaskMapper;
import org.team3todo.secure.secure_team_3_todo_api.service.TaskService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping("/team-tasks/{teamId}")
    public ResponseEntity<List<TaskDto>> findTasksByTeamId(@PathVariable Long teamId){
        List<Task> foundTasks = taskService.findEnrichedTasksByTeamId(teamId);
        List<TaskDto> dtoTasks = taskMapper.convertToDtoList(foundTasks);
        return ResponseEntity.ok(dtoTasks);
    }

    @GetMapping("/user-tasks/{userGuid}")
    public ResponseEntity<List<TaskDto>> findTasksByUserGuid(
            @PathVariable UUID userGuid,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String team) {

        List<Task> foundTasks = taskService.findEnrichedTasksByAssignedUser(
                userGuid, name, status, team
        );

        List<TaskDto> dtoTasks = taskMapper.convertToDtoList(foundTasks);
        return ResponseEntity.ok(dtoTasks);
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @RequestBody TaskCreateRequestDto taskRequest,
            Authentication authentication) {

        // Get the currently authenticated user who is creating the task.
        User creator = (User) authentication.getPrincipal();

        // Call the service to create the task.
        Task createdTask = taskService.createTask(taskRequest, creator);

        // Enrich the single created task with its status before mapping
        // (The service already set the initial status, but this ensures consistency)
        createdTask = taskService.enrichTaskWithCurrentStatus(createdTask);

        // Map the created entity to a DTO for the response.
        TaskDto responseDto = taskMapper.convertToDto(createdTask); // Pass empty map or handle status mapping

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/{taskGuid}/assign-me")
    public ResponseEntity<TaskDto> assignTaskToCurrentUser(
            @PathVariable UUID taskGuid,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        Task updatedTask = taskService.assignCurrentUserToTask(taskGuid, currentUser);
        TaskDto responseDto = taskMapper.convertToDto(updatedTask);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{taskGuid}/unassign-me")
    public ResponseEntity<TaskDto> unassignTaskFromCurrentUser(
            @PathVariable UUID taskGuid,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        Task updatedTask = taskService.unassignCurrentUserFromTask(taskGuid, currentUser);
        TaskDto responseDto = taskMapper.convertToDto(updatedTask);
        return ResponseEntity.ok(responseDto);
    }
}
