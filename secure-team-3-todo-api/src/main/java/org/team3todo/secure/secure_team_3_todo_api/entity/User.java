package org.team3todo.secure.secure_team_3_todo_api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.team3todo.secure.secure_team_3_todo_api.util.StringCryptoConverter;

import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users", indexes = {
        @Index(name = "idx_users_username", columnList = "username"),
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_user_guid", columnList = "user_guid")
})
public class User implements UserDetails {
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

    @ManyToOne(fetch = FetchType.EAGER) // Eager fetch for system role as it's fundamental for security
    @JoinColumn(name = "system_role_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_users_system_role_id"))
    private SystemRole systemRole;

    @Column(name = "login_attempts", columnDefinition = "INTEGER DEFAULT 0")
    @Builder.Default
    private Integer loginAttempts = 0;

    @Column(name = "is_locked", columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean isLocked = false;

    @Column(name = "is_totp_enabled", columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private boolean isTotpEnabled = false;

    @Column(name = "totp_secret", columnDefinition = "TEXT")
    @Convert(converter = StringCryptoConverter.class) 
    private String totpSecret;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    // vvvvv RELATIONSHIPS vvvvv
    @OneToMany(mappedBy = "createdByUserId")
    @Builder.Default
    private List<Team> createdTeams = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY) // Changed back to LAZY
    @Builder.Default
    private List<TeamMembership> teamMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "userCreator")
    @Builder.Default
    private List<Task> createdTasks = new ArrayList<>();

    @OneToMany(mappedBy = "assignedToUser")
    @Builder.Default
    private List<Task> assignedTasks = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.userGuid == null) {
            this.userGuid = UUID.randomUUID();
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Authority based on the main System Role (e.g., "SYSTEM_ADMIN", "REGULAR_USER")
        if (this.systemRole != null && this.systemRole.getName() != null){
            return List.of(new SimpleGrantedAuthority("ROLE_"+ this.systemRole.getName()));
        }
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return passwordHash;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userGuid != null && Objects.equals(userGuid, user.userGuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userGuid);
    }
}
