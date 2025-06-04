package org.team3todo.secure.secure_team_3_todo_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tasks", indexes = {
        @Index(name = "idx_tasks_user_id", columnList = "user_id"),
        @Index(name = "idx_tasks_team_id", columnList = "team_id"),
        @Index(name = "idx_tasks_due_date", columnList = "due_date"),
        @Index(name = "idx_tasks_assigned_to_user_id", columnList = "assigned_to_user_id"),
        @Index(name = "idx_tasks_task_guid", columnList = "task_guid")
})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false, foreignKey = @ForeignKey(name="fk_tasks_user_id"))
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private User userCreator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="team_id", nullable = false, foreignKey = @ForeignKey(name="fk_tasks_team_id"))
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private Team team;

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
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private User assignedToUser;

    // vvvvv RELATIONSHIPS vvvvv
    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE) // Assuming ON DELETE CASCADE from task_status_history
    private List<TaskStatusHistory> statusHistory = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (taskGuid == null) {
            taskGuid = UUID.randomUUID();
        }
    }
}
