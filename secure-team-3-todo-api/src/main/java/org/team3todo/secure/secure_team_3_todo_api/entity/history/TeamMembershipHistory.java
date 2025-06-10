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
@Table(name = "team_memberships_history")
public class TeamMembershipHistory {
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
    private Long originalMembershipId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "assigned_at", nullable = false)
    private OffsetDateTime assignedAt;
}