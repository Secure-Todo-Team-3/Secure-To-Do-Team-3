package org.team3todo.secure.secure_team_3_todo_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Important for write operations
import org.team3todo.secure.secure_team_3_todo_api.entity.Role;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.TeamMembership;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.repository.RoleRepository; // Assuming you have these
import org.team3todo.secure.secure_team_3_todo_api.repository.TeamMembershipRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.TeamRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.UserRepository;

import java.util.Optional; // For fetching entities

@Service
public class TeamMembershipService {

    private final TeamMembershipRepository teamMembershipRepository;
    private final UserRepository userRepository; // To fetch User entities by ID/GUID
    private final TeamRepository teamRepository; // To fetch Team entities by ID
    private final RoleRepository roleRepository; // To fetch Role entities by ID

    @Autowired
    public TeamMembershipService(TeamMembershipRepository teamMembershipRepository,
                                 UserRepository userRepository,
                                 TeamRepository teamRepository,
                                 RoleRepository roleRepository) {
        this.teamMembershipRepository = teamMembershipRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public TeamMembership addUserToTeam(Long userId, Long teamId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        if (teamMembershipRepository.existsByUserAndTeamAndRole(user, team, role)) {
            throw new RuntimeException("User " + user.getUsername() +
                    " is already a member of team " + team.getName() +
                    " with role " + role.getName());
        }
        TeamMembership newMembership = TeamMembership.builder()
                .user(user)
                .team(team)
                .role(role)
                .build();

        return teamMembershipRepository.save(newMembership);
    }


}