package org.team3todo.secure.secure_team_3_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.team3todo.secure.secure_team_3_todo_api.entity.TeamMembership;

import java.util.List;

public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {
    List<TeamMembership> findByTeamId(Long teamId);
}
