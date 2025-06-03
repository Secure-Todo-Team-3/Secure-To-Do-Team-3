package org.team3todo.secure.secure_team_3_todo_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.repository.TeamRepository;
import java.util.Optional;


@Service
public class TeamService {
    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public TeamDto findById(Long id) {
        Optional<Team> team = teamRepository.findById(id);
        return team.map(this::convertToDto).orElse(null);
    }

    public TeamDto convertToDto(Team team) {
        if (team == null) {
            return null;
        }
        return TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .createdByUserId(team.getCreatedByUserId() != null ? team.getCreatedByUserId().getUserGuid() : null)
                .createdByUsername(team.getCreatedByUserId() != null ? team.getCreatedByUserId().getUsername() : null)
                .isActive(team.getIsActive())
                .createdAt(team.getCreatedAt())
                .build();
    }
}
