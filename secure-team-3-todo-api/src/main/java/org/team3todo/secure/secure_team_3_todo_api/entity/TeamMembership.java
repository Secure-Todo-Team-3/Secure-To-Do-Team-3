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
@Table(name="team_memberships", indexes = {
        @Index(name = "idx_team_memberships_user_id", columnList = "user_id"),
        @Index(name = "idx_team_memberships_team_id", columnList = "team_id"),
        @Index(name = "idx_team_memberships_role_id", columnList = "role_id")
})
public class TeamMembership {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "user_id", nullable = false, foreignKey = @ForeignKey(name="fk_tm_user_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "team_id", nullable = false, foreignKey = @ForeignKey(name="fk_tm_team_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_tm_role_id"))
    private Role role;

    @CreationTimestamp
    @Column(name = "assigned_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime assignedAt;


    //TODO: The constraints and dependencies between the fields still to be done.
}

