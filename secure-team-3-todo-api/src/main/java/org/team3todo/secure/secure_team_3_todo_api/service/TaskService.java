package org.team3todo.secure.secure_team_3_todo_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.dto.TaskDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Task;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.exception.ResourceNotFoundException;
import org.team3todo.secure.secure_team_3_todo_api.repository.TaskRepository;

import java.util.List;
import java.util.UUID;


@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TeamService teamService;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService, TeamService teamService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.teamService = teamService;
    }

    public List<Task> findByTeamId(Long teamId) {
        Team foundTeam = teamService.findById(teamId);
        if (foundTeam != null){
            return taskRepository.findByTeam(foundTeam);
        }
        else{
            throw new ResourceNotFoundException("Cannot find tasks for a non-existent team with ID: " + teamId);
        }
    }

    public List<Task> findByAssignedUserGuid(UUID guid){
        User foundUser = userService.findByUserGuid(guid);
        if (foundUser != null){
            return taskRepository.findByAssignedToUser(foundUser);
        }
        else{
            throw new ResourceNotFoundException("User not found with GUID: "+ guid);
        }
    }
}
