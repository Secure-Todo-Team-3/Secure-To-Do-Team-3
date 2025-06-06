package org.team3todo.secure.secure_team_3_todo_api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="teams", indexes = {
        @Index(name = "idx_teams_name", columnList = "name"),
        @Index(name = "idx_teams_created_by_user_id", columnList = "created_by_user_id")
})
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY) // We are mapping to a user
    @JoinColumn(name = "created_by_user_id", foreignKey = @ForeignKey(name="fk_teams_created_by_user_id")) // This is our join column using our defined FK constraint
    @OnDelete(action = OnDeleteAction.SET_NULL) // If this user is deleted, we set this field to null. Keep the team.
    @JsonBackReference
    private User createdByUserId;

    @Column(name = "is_active")
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    // vvvvv RELATIONSHIPS vvvvvv
    @OneToMany(mappedBy = "team", cascade = CascadeType.REMOVE) // Assuming ON DELETE CASCADE from team_memberships
    private List<TeamMembership> teamMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    private List<Task> tasks = new ArrayList<>();

}
