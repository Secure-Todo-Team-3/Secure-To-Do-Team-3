package org.team3todo.secure.secure_team_3_todo_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskStatusDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.UserDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.TaskStatus;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.exception.ResourceNotFoundException;
import org.team3todo.secure.secure_team_3_todo_api.mapper.TaskStatusMapper;
import org.team3todo.secure.secure_team_3_todo_api.mapper.UserMapper;
import org.team3todo.secure.secure_team_3_todo_api.service.TaskStatusService;
import org.team3todo.secure.secure_team_3_todo_api.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/task-status")
public class TaskStatusController {

    private final TaskStatusService taskStatusService;
    private final TaskStatusMapper taskStatusMapper;

    public TaskStatusController(TaskStatusService taskStatusService, TaskStatusMapper taskStatusMapper) {
        this.taskStatusService = taskStatusService;
        this.taskStatusMapper = taskStatusMapper;
    }

    @GetMapping("")
    public ResponseEntity<List<TaskStatusDto>> getAllStatuses(){
        List<TaskStatus> taskStatus = taskStatusService.getAllStatuses();
        if (taskStatus == null){
            return ResponseEntity.ok(Collections.emptyList());
        }
        else{
            return ResponseEntity.ok(taskStatusMapper.convertToDtoList(taskStatus));
        }
    }
}
