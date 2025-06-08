package org.team3todo.secure.secure_team_3_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.team3todo.secure.secure_team_3_todo_api.entity.TeamRole;

import java.util.Optional;

public interface TeamRoleRepository extends JpaRepository<TeamRole, Long> {
    Optional<TeamRole> findByName(String name);
}
