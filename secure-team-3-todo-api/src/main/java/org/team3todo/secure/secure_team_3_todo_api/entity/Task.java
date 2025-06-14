package org.team3todo.secure.secure_team_3_todo_api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tasks", indexes = {
        @Index(name = "idx_tasks_user_id", columnList = "user_id"),
        @Index(name = "idx_tasks_team_id", columnList = "team_id"),
        @Index(name = "idx_tasks_due_date", columnList = "due_date"),
        @Index(name = "idx_tasks_assigned_to_user_id", columnList = "assigned_to_user_id"),
        @Index(name = "idx_tasks_task_guid", columnList = "task_guid"),
        @Index(name = "idx_tasks_task_status_id", columnList = "task_status_id")
})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="creator_user_id", nullable = false, foreignKey = @ForeignKey(name="fk_tasks_user_id"))
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private User userCreator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="team_id", nullable = false, foreignKey = @ForeignKey(name="fk_tasks_team_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_status_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tasks_status_id"))
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private TaskStatus taskStatus;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(columnDefinition = "uuid", name = "task_guid", unique = true, nullable = false, updatable = false)
    private UUID taskGuid;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime updatedAt;

    @Column(name = "due_date", nullable = false)
    private OffsetDateTime dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tasks_assigned_to_user_id"))
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User assignedToUser;

    @PrePersist
    public void prePersist() {
        if (taskGuid == null) {
            taskGuid = UUID.randomUUID();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        // Use a stable, non-null business key. taskGuid is perfect.
        return taskGuid != null && Objects.equals(taskGuid, task.taskGuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskGuid);
    }
}
