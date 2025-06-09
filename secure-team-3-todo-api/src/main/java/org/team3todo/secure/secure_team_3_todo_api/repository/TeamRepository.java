package org.team3todo.secure.secure_team_3_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findById(Long id);
    List<Team> findByTeamMemberships_User(User user);
    List<Team> findByCreatedByUserId(User user);
    List<Team> findByTeamMemberships_UserAndTeamMemberships_TeamRole_IdNotIn(User user, Collection<Long> roleIdsToExclude);
    Boolean existsByName(String name);
}
