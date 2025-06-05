package org.team3todo.secure.secure_team_3_todo_api.mapper;

import org.springframework.stereotype.Component;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamMembershipDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.TeamMembership;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.Role;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TeamMembershipMapper {

    public TeamMembershipDto convertToDto(TeamMembership teamMembership) {
        if (teamMembership == null) {
            return null;
        }

        User user = teamMembership.getUser();
        Team team = teamMembership.getTeam();
        Role role = teamMembership.getRole();

        TeamMembershipDto.TeamMembershipDtoBuilder builder = TeamMembershipDto.builder()
                .id(teamMembership.getId())
                .assignedAt(teamMembership.getAssignedAt());

        if (user != null) {
            builder.userGuid(user.getUserGuid()); // Assuming User entity has getUserGuid()
            builder.username(user.getUsername());   // Assuming User entity has getUsername()
        }

        if (team != null) {
            builder.teamId(team.getId());         // Assuming Team entity has getId()
            builder.teamName(team.getName());     // Assuming Team entity has getName()
        }

        if (role != null) {
            builder.roleId(role.getId());         // Assuming Role entity has getId() (and it's Integer)
            builder.roleName(role.getName());     // Assuming Role entity has getName()
        }

        return builder.build();
    }

    public List<TeamMembershipDto> convertToDtoList(List<TeamMembership> teamMemberships) {
        if (teamMemberships == null || teamMemberships.isEmpty()) {
            return Collections.emptyList();
        }
        return teamMemberships.stream()
                .map(this::convertToDto) // Use 'this' for instance method
                .collect(Collectors.toList());
    }

    // You might also want a method to convert from a DTO to an Entity,
    // especially if you have a DTO for creating team memberships.
    // That would be more complex as you'd need to fetch User, Team, and Role entities
    // based on IDs provided in the DTO, or pass them as arguments.
    /*
    public TeamMembership convertToEntity(TeamMembershipCreateDto dto, User user, Team team, Role role) {
        if (dto == null) {
            return null;
        }
        return TeamMembership.builder()
                .user(user)
                .team(team)
                .role(role)
                // 'id' and 'assignedAt' would be generated
                .build();
    }
    */
}