package org.team3todo.secure.secure_team_3_todo_api.entity.history;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Immutable
@Table(name = "tasks_history")
public class TaskHistory {
    @Id
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "operation_type", nullable = false, length = 10)
    private String operationType;

    @Column(name = "changed_by_user_id")
    private Long changedByUserId;

    @Column(name = "changed_at", nullable = false)
    private OffsetDateTime changedAt;

    // --- Snapshot Columns ---
    @Column(name = "id", nullable = false)
    private Long originalTaskId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "task_status_id", nullable = false)
    private Integer taskStatusId;

    @Column(nullable = false)
    private String name;

    @Column(name = "task_guid", nullable = false)
    private UUID taskGuid;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "assigned_to_user_id")
    private Long assignedToUserId;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;
}