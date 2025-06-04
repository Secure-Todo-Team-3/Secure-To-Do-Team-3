package org.team3todo.secure.secure_team_3_todo_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Task;
import org.team3todo.secure.secure_team_3_todo_api.mapper.TaskMapper;
import org.team3todo.secure.secure_team_3_todo_api.service.TaskService;
import org.team3todo.secure.secure_team_3_todo_api.service.TeamService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/task")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping("/team-tasks/{teamId}")
    public ResponseEntity<List<TaskDto>> findTasksByTeamId(@PathVariable Long teamId){
        List<Task> foundTasks = taskService.findByTeamId(teamId);
        List<TaskDto> dtoTasks = taskMapper.convertToDtoList(foundTasks);
        return ResponseEntity.ok(dtoTasks);
    }

    @GetMapping("/user-tasks/{userGuid}")
    public ResponseEntity<List<TaskDto>> findTasksByUserGuid(@PathVariable UUID userGuid){
        List<Task> foundTasks = taskService.findByUserGuid(userGuid);
        List<TaskDto> dtoTasks = taskMapper.convertToDtoList(foundTasks);
        return ResponseEntity.ok(dtoTasks);
    }
}
