package org.team3todo.secure.secure_team_3_todo_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamDto;
import org.team3todo.secure.secure_team_3_todo_api.service.TaskService;
import org.team3todo.secure.secure_team_3_todo_api.service.TeamService;

import java.util.List;

@RestController
@RequestMapping("api/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping()
    public ResponseEntity<List<TaskDto>> getTeamByGUID(@RequestParam Long teamId){
        List<TaskDto> foundTasks = taskService.findByTeamId(teamId);
        return ResponseEntity.ok(foundTasks);
    }
}
