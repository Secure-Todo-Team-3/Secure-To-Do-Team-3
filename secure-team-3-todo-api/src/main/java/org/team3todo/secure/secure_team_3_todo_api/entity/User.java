package org.team3todo.secure.secure_team_3_todo_api.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
//import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users", indexes = {
        @Index(name = "idx_users_username", columnList = "username"),
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_user_guid", columnList = "user_guid")
})
//public class User implements UserDetails{ to be used when we do auth
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 255)
    private String username;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(nullable = false, name = "password_hash", columnDefinition = "TEXT")
    private String passwordHash;

    @Column(nullable = false, name = "password_salt", columnDefinition = "TEXT")
    private String passwordSalt;

    @Column(unique = true, name = "user_guid", columnDefinition = "uuid")
    private UUID userGuid;

    @Column(name = "login_attempts",columnDefinition = "INTEGER DEFAULT 0")
    private Integer loginAttempts = 0;

    @Column(name = "is_locked", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isLocked = false;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    // vvvvv RELATIONSHIPS vvvvv
    @OneToMany(mappedBy = "createdByUserId")
    private List<Team> createdTeams = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE) // Assuming ON DELETE CASCADE from team_memberships
    @JsonManagedReference
    private List<TeamMembership> teamMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "userCreator")
    @JsonManagedReference
    private List<Task> createdTasks = new ArrayList<>();

    @OneToMany(mappedBy = "assignedToUser")
    @JsonManagedReference
    private List<Task> assignedTasks = new ArrayList<>();

    @OneToMany(mappedBy = "changedByUser")
    @JsonManagedReference
    private List<TaskStatusHistory> taskStatusHistories = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.userGuid == null) {
            this.userGuid = UUID.randomUUID();
        }
    }

}