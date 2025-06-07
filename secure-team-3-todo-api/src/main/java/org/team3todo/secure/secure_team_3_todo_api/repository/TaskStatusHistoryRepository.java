package org.team3todo.secure.secure_team_3_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.team3todo.secure.secure_team_3_todo_api.entity.Task;
import org.team3todo.secure.secure_team_3_todo_api.entity.TaskStatusHistory;

import java.util.List;
import java.util.Optional;

public interface TaskStatusHistoryRepository extends JpaRepository<TaskStatusHistory, Long> {

    /**
     * Finds the most recent status history record for a given task ID.
     * Spring Data JPA will create a query that orders by change_timestamp in descending order
     * and returns the top (first) result.
     *
     * @param task The task.
     * @return An Optional containing the latest TaskStatusHistory record if one exists.
     */
    Optional<TaskStatusHistory> findFirstByTaskOrderByChangeTimestampDesc(Task task);

    /**
     * Finds the latest status history record for each task in a given list of task IDs.
     * This is highly efficient as it uses a subquery to find the latest timestamp for each task
     * and fetches them all in a single database query.
     *
     * @param taskIds A list of task IDs to find the latest status for.
     * @return A List of the latest TaskStatusHistory records for the given tasks.
     */
    @Query("SELECT h FROM TaskStatusHistory h " +
            "WHERE h.task.id IN :taskIds AND h.changeTimestamp = (" +
            "  SELECT MAX(h2.changeTimestamp) FROM TaskStatusHistory h2 WHERE h2.task = h.task" +
            ")")
    List<TaskStatusHistory> findLatestStatusForTasks(@Param("taskIds") List<Long> taskIds);
}