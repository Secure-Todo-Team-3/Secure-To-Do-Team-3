package org.team3todo.secure.secure_team_3_todo_api.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 255)
    private String username;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(nullable = false, name = "password_hash", columnDefinition = "TEXT")
    private String passwordHash;

    @Column(unique = true, name = "user_guid", columnDefinition = "uuid")
    private UUID userGuid;

    @Column(name = "login_attempts",columnDefinition = "INTEGER DEFAULT 0")
    @Builder.Default
    private Integer loginAttempts = 0;

    @Column(name = "is_locked", columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean isLocked = false;


    @Column(name = "totp_enabled", columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private boolean totpEnabled = false;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "createdByUserId")
    @Builder.Default
    private List<Team> createdTeams = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JsonManagedReference 
    @Builder.Default
    private List<TeamMembership> teamMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "userCreator")
    @JsonManagedReference
    @Builder.Default
    private List<Task> createdTasks = new ArrayList<>();

    @OneToMany(mappedBy = "assignedToUser")
    @JsonManagedReference
    @Builder.Default
    private List<Task> assignedTasks = new ArrayList<>();

    @OneToMany(mappedBy = "changedByUser")
    @JsonManagedReference
    @Builder.Default
    private List<TaskStatusHistory> taskStatusHistories = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.userGuid == null) {
            this.userGuid = UUID.randomUUID();
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (teamMemberships == null || teamMemberships.isEmpty()) {
            return List.of(); 
        }
        return teamMemberships.stream()
                .filter(tm -> tm.getRole() != null) 
                .map(TeamMembership::getRole)
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() {
      
        return !Boolean.TRUE.equals(this.isLocked);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; 
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.isActive);
    }
}
