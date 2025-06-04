package org.team3todo.secure.secure_team_3_todo_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_status_history", indexes = {
        @Index(name = "idx_tsh_task_timestamp", columnList = "task_id, change_timestamp"),
        @Index(name = "idx_task_status_history_task_id", columnList = "task_id"),
        @Index(name = "idx_task_status_history_status_id", columnList = "status_id"),
        @Index(name = "idx_task_status_history_changed_by_user_id", columnList = "changed_by_user_id"),
        @Index(name = "idx_task_status_history_change_timestamp", columnList = "change_timestamp")
})
public class TaskStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tsh_task_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tsh_status_id"))
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tsh_changed_by_user_id"))
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private User changedByUser;

    @CreationTimestamp
    @Column(name = "change_timestamp", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime changeTimestamp;
}
