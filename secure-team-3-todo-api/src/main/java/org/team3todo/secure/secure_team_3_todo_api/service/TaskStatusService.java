package org.team3todo.secure.secure_team_3_todo_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.entity.TaskStatus;
import org.team3todo.secure.secure_team_3_todo_api.repository.TaskStatusRepository;

import java.util.List;

@Service
public class TaskStatusService {

    private TaskStatusRepository taskStatusRepository;

    @Autowired
    public TaskStatusService(TaskStatusRepository taskStatusRepository){
        this.taskStatusRepository = taskStatusRepository;
    }

    public List<TaskStatus> getAllStatuses(){
        return this.taskStatusRepository.findAll();
    }
}
