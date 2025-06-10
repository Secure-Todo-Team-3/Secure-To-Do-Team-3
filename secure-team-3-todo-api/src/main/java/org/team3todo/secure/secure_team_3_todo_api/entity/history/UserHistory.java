package org.team3todo.secure.secure_team_3_todo_api.entity.history;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Immutable // This entity is read-only from the application side
@Table(name = "users_history")
public class UserHistory {
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
    private Long originalUserId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(name = "user_guid", nullable = false)
    private UUID userGuid;

    @Column(name = "is_locked", nullable = false)
    private boolean isLocked;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "system_role_id", nullable = false)
    private Integer systemRoleId;
}