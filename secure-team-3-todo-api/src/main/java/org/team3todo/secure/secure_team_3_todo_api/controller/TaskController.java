package org.team3todo.secure.secure_team_3_todo_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskCreateRequestDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskUpdateRequestDto;
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
        List<Task> foundTasks = taskService.findTasksByTeamId(teamId);
        List<TaskDto> dtoTasks = taskMapper.convertToDtoList(foundTasks);
        return ResponseEntity.ok(dtoTasks);
    }

    @GetMapping("/{taskGuid}")
    public ResponseEntity<TaskDto> getTaskByGuid(@PathVariable UUID taskGuid) {
        Task foundTask = taskService.findByGuid(taskGuid);
        if (foundTask != null) {
            TaskDto taskDto = taskMapper.convertToDto(foundTask);
            return ResponseEntity.ok(taskDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user-tasks")
    public ResponseEntity<List<TaskDto>> findTasksByUserGuid(
            Authentication authentication,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String team) {

        UUID userGuid = (UUID) authentication.getPrincipal();
        List<Task> foundTasks = taskService.findTasksByAssignedUser(userGuid, name, status, team);

        List<TaskDto> dtoTasks = taskMapper.convertToDtoList(foundTasks);
        return ResponseEntity.ok(dtoTasks);
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @RequestBody TaskCreateRequestDto taskRequest,
            Authentication authentication) {
        UUID creatorGuid = (UUID) authentication.getPrincipal();
        Task createdTask = taskService.createTask(taskRequest, creatorGuid);
        TaskDto responseDto = taskMapper.convertToDto(createdTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/{taskGuid}")
    public ResponseEntity<TaskDto> updateTask(@RequestBody TaskUpdateRequestDto taskRequest,
                                              Authentication authentication,
                                              @PathVariable UUID taskGuid){
        UUID creatorGuid = (UUID) authentication.getPrincipal();
        Task createdTask = taskService.updateTask(taskRequest, creatorGuid, taskGuid);
        TaskDto responseDto = taskMapper.convertToDto(createdTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/{taskGuid}/assign-me")
    public ResponseEntity<TaskDto> assignTaskToCurrentUser(
            @PathVariable UUID taskGuid,
            Authentication authentication) {
        UUID currentUserUUID = (UUID) authentication.getPrincipal(); // Uses UUID now
        Task updatedTask = taskService.assignCurrentUserToTask(taskGuid, currentUserUUID);
        TaskDto responseDto = taskMapper.convertToDto(updatedTask);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{taskGuid}/unassign-me")
    public ResponseEntity<TaskDto> unassignTaskFromCurrentUser(
            @PathVariable UUID taskGuid,
            Authentication authentication) {
        UUID currentUserUUID = (UUID) authentication.getPrincipal(); // Uses UUID Now
        Task updatedTask = taskService.unassignCurrentUserFromTask(taskGuid, currentUserUUID);
        TaskDto responseDto = taskMapper.convertToDto(updatedTask);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{taskGuid}/add-to-team/{teamId}")
    public ResponseEntity<TaskDto> addTaskToTeam(@PathVariable UUID taskGuid, @PathVariable Long teamId, Authentication authentication){
        UUID currentUserUUID = (UUID) authentication.getPrincipal(); // Uses UUID Now
        Task reassignedTask = taskService.assignTaskToTeam(taskGuid, teamId, currentUserUUID);
        TaskDto responseDto = taskMapper.convertToDto(reassignedTask);
        return ResponseEntity.ok(responseDto);
    }
}
