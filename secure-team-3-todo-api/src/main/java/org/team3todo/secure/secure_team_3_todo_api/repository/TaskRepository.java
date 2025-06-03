package org.team3todo.secure.secure_team_3_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.team3todo.secure.secure_team_3_todo_api.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByTeamId(Long teamId);
}
