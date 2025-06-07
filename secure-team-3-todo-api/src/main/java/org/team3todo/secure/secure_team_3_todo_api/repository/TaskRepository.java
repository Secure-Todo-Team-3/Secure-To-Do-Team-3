package org.team3todo.secure.secure_team_3_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.team3todo.secure.secure_team_3_todo_api.entity.Task;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    Optional<Task> findByTaskGuid(UUID guid);
    List<Task> findByTeam(Team team);
    List<Task> findByAssignedToUser(User user);

}