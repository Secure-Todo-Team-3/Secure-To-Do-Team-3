package org.team3todo.secure.secure_team_3_todo_api.entity.history;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Immutable
@Table(name = "teams_history")
public class TeamHistory {
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
    private Long originalTeamId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}