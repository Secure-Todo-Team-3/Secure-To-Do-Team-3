package org.team3todo.secure.secure_team_3_todo_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "system_roles", indexes = {
        @Index(name = "idx_system_roles_name", columnList = "name")
})
@Data // Includes @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class SystemRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // SERIAL maps to Integer for the ID

    @Column(length = 100, unique = true, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;
}