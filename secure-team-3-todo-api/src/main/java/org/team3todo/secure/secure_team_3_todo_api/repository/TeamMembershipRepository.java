package org.team3todo.secure.secure_team_3_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.TeamMembership;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.entity.TeamRole;

import java.util.List;
import java.util.Optional;

public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {
    List<TeamMembership> findByTeamId(Team team);
    List<TeamMembership> findByUser(User user);
    Optional<TeamMembership> findByUserAndTeam(User user, Team team);
    Optional<TeamMembership> findByUserAndTeamAndRole(User user, Team team, TeamRole teamRole);
    boolean existsByUserAndTeamAndRole(User user, Team team, TeamRole teamRole);
}
