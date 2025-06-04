package org.team3todo.secure.secure_team_3_todo_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles", indexes = {
    @Index(name = "idx_roles_name", columnList = "name")
})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // vvvvv RELATIONSHIPS vvvvv
    @OneToMany(mappedBy = "role")
    private List<TeamMembership> teamMemberships = new ArrayList<>();
}
