package org.team3todo.secure.secure_team_3_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.team3todo.secure.secure_team_3_todo_api.entity.Task;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {


    // Fetch the task and its key relationships in one query to avoid N+1 issues.
    @Query("SELECT t FROM Task t " +
            "LEFT JOIN FETCH t.taskStatus " +
            "LEFT JOIN FETCH t.team " +
            "LEFT JOIN FETCH t.assignedToUser " +
            "LEFT JOIN FETCH t.userCreator " +
            "WHERE t.taskGuid = :guid")
    Optional<Task> findByTaskGuidWithDetails(@Param("guid") UUID guid);

    List<Task> findByTeam(Team team);

    List<Task> findByAssignedToUser(User user);

    @Query("SELECT t FROM Task t JOIN FETCH t.team WHERE t.taskGuid = :taskGuid")
    Optional<Task> findByTaskGuidWithTeam(@Param("taskGuid") UUID taskGuid);
}