package org.team3todo.secure.secure_team_3_todo_api.mapper; // Or your preferred mapper package

import org.springframework.stereotype.Component;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.User; // Assuming User is in this package

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TeamMapper {

    public TeamDto convertToDto(Team team) {
        if (team == null) {
            return null;
        }

        User createdByUser = team.getCreatedByUserId(); // Renamed from getCreatedByUserId to getCreatedByUser for clarity

        return TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .createdByUserId(createdByUser != null ? createdByUser.getUserGuid() : null)
                .createdByUsername(createdByUser != null ? createdByUser.getUsername() : null)
                .isActive(team.getIsActive())
                .createdAt(team.getCreatedAt())
                .build();
    }

    public List<TeamDto> convertToDtoList(List<Team> teams) {
        if (teams == null) {
            return Collections.emptyList();
        }
        return teams.stream()
                .map(this::convertToDto) // Reference the static method
                .collect(Collectors.toList());
    }

    // If you needed to map from DTO to Entity (for create/update operations):
    // TODO: Test if this works.
    public static Team convertToEntity(TeamDto teamDto, User creator) { // Pass User entity if needed
        if (teamDto == null) {
            return null;
        }
        Team team = Team.builder()
                .name(teamDto.getName())
                .description(teamDto.getDescription())
                .isActive(teamDto.getIsActive() != null ? teamDto.getIsActive() : true) // Default to true if not provided
                // .createdByUserId(creator) // Set the creator User entity
                // ID and createdAt would be set by JPA/DB
                .build();
        // If creator is not passed, you might set it in the service before saving
        if (creator != null) {
            team.setCreatedByUserId(creator);
        }
        return team;
    }

}