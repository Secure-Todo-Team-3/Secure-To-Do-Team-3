package org.team3todo.secure.secure_team_3_todo_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Important for write operations
import org.team3todo.secure.secure_team_3_todo_api.entity.TeamRole;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.TeamMembership;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.exception.DuplicateResourceException;
import org.team3todo.secure.secure_team_3_todo_api.exception.ResourceNotFoundException;
import org.team3todo.secure.secure_team_3_todo_api.repository.TeamMembershipRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.TeamRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.TeamRoleRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.UserRepository;

import java.util.UUID;

@Service
public class TeamMembershipService {

    private final TeamMembershipRepository teamMembershipRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamRoleRepository teamRoleRepository;

    @Autowired
    public TeamMembershipService(TeamMembershipRepository teamMembershipRepository,
                                 UserRepository userRepository,
                                 TeamRepository teamRepository,
                                 TeamRoleRepository teamRoleRepository) {
        this.teamMembershipRepository = teamMembershipRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.teamRoleRepository = teamRoleRepository;
    }

    @Transactional
    public TeamMembership addUserToTeam(Long userId, Long teamId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        TeamRole teamRole = teamRoleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        if (teamMembershipRepository.existsByUserAndTeamAndTeamRole(user, team, teamRole)) {
            throw new DuplicateResourceException("User " + user.getUsername() +
                    " is already a member of team " + team.getName() +
                    " with role " + teamRole.getName());
        }
        TeamMembership newMembership = TeamMembership.builder()
                .user(user)
                .team(team)
                .teamRole(teamRole)
                .build();

        return teamMembershipRepository.save(newMembership);
    }

    @Transactional
    public TeamMembership updateUserRoleInTeam(UUID userGuid, Long teamId, Long newRoleId) {
        User user = userRepository.findByUserGuid(userGuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userGuid));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + teamId));
        TeamRole newTeamRole = teamRoleRepository.findById(newRoleId)
                .orElseThrow(() -> new ResourceNotFoundException("TeamRole not found with ID: " + newRoleId));

        TeamMembership membershipToUpdate = teamMembershipRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new ResourceNotFoundException("User '" + user.getUsername() + "' is not a member of team '" + team.getName() + "'."));

        membershipToUpdate.setTeamRole(newTeamRole);
        return teamMembershipRepository.save(membershipToUpdate);
    }

    public boolean isUserInTeam(User user, Team team){
        return teamMembershipRepository.existsByUserAndTeam(user,team);
    }
}