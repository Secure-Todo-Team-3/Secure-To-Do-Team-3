package org.team3todo.secure.secure_team_3_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.team3todo.secure.secure_team_3_todo_api.entity.Task;
import java.time.LocalDate;
import java.util.List;

public interface TaskStatisticsRepository extends JpaRepository<Task, Long> {

    @Query(value = """
        WITH date_series AS (
            -- 1. Generate a series of dates for the reporting period
            SELECT generate_series(:startDate\\:\\:date, CURRENT_DATE, '1 day'\\:\\:interval)\\:\\:date AS report_date
        ),
        all_task_events AS (
            -- 2. Combine the current state with all historical events into one source
            -- The current, live state of tasks
            SELECT id, updated_at as event_time, task_status_id, assigned_to_user_id, team_id
            FROM tasks
            WHERE team_id IN (:teamIds)
            UNION ALL
            -- All historical states for those tasks
            SELECT id, changed_at as event_time, task_status_id, assigned_to_user_id, team_id
            FROM tasks_history
            WHERE team_id IN (:teamIds)
        ),
        last_status_per_day AS (
            -- 3. For each task and each day, find the absolute latest status event on or before that day
            SELECT DISTINCT ON (d.report_date, e.id)
                d.report_date,
                e.id AS task_id,
                e.team_id,
                e.assigned_to_user_id,
                e.task_status_id AS final_status_id
            FROM date_series d
            JOIN all_task_events e ON e.event_time\\:\\:date <= d.report_date
            JOIN tasks t_meta ON t_meta.id = e.id AND t_meta.created_at\\:\\:date <= d.report_date
            ORDER BY d.report_date, e.id, e.event_time DESC
        )
        -- 4. Finally, aggregate the results
        SELECT 
            ls.report_date AS "reportDate",
            ls.team_id AS "teamId",
            u.user_guid AS "userGuid",
            u.username AS "username",
            CASE WHEN ts.id IN (5, 6, 7) THEN 'CLOSED' ELSE 'OPEN' END AS "statusCategory",
            count(*)\\:\\:int AS "taskCount"
        FROM last_status_per_day ls
        JOIN task_statuses ts ON ls.final_status_id = ts.id
        JOIN users u ON ls.assigned_to_user_id = u.id
        WHERE ls.assigned_to_user_id IS NOT NULL
        GROUP BY ls.report_date, ls.team_id, u.user_guid, u.username, "statusCategory"
        ORDER BY ls.report_date DESC
    """, nativeQuery = true)
    List<DailyUserStatProjection> getDailyTaskStatistics(
            @Param("teamIds") List<Long> teamIds,
            @Param("startDate") LocalDate startDate
    );
}