package org.team3todo.secure.secure_team_3_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.TeamMembership;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.entity.TeamRole;

import java.util.List;
import java.util.Optional;

public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {
    List<TeamMembership> findByTeam(Team team);
    List<TeamMembership> findByUser(User user);
    Optional<TeamMembership> findByUserAndTeam(User user, Team team);
    Optional<TeamMembership> findByUserAndTeamAndTeamRole(User user, Team team, TeamRole teamRole);
    @Query("SELECT tm.team FROM TeamMembership tm WHERE tm.user.id = :userId AND tm.teamRole.name = :roleName")
    List<Team> findTeamsByUserIdAndRoleName(@Param("userId") Long userId, @Param("roleName") String roleName);
    boolean existsByUserAndTeam(User user, Team team);
    boolean existsByUserIdAndTeamId(Long userId, Long teamId);
    boolean existsByUserAndTeamAndTeamRole(User user, Team team, TeamRole teamRole);
}
