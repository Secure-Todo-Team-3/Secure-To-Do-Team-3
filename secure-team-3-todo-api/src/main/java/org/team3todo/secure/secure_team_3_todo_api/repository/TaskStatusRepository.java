package org.team3todo.secure.secure_team_3_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.team3todo.secure.secure_team_3_todo_api.entity.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Integer> {
    List<TaskStatus> findAll();
    Optional<TaskStatus> findByName(String name);
    Optional<TaskStatus> findById(Long id);
}
