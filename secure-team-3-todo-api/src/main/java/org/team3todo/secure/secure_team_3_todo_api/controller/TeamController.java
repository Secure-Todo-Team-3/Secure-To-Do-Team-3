package org.team3todo.secure.secure_team_3_todo_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.mapper.TeamMapper;
import org.team3todo.secure.secure_team_3_todo_api.service.TeamService;
import org.team3todo.secure.secure_team_3_todo_api.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/team")
public class TeamController {

    private final TeamService teamService;
    private final TeamMapper teamMapper;

    public TeamController(TeamService teamService, TeamMapper teamMapper) {
        this.teamService = teamService; this.teamMapper = teamMapper;
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDto> getTeamById(@PathVariable Long teamId){
        Team foundTeam = teamService.findById(teamId);
        if(foundTeam != null){
            return ResponseEntity.ok(teamMapper.convertToDto(foundTeam));
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user-teams/{userGuid}")
    public ResponseEntity<List<TeamDto>> getTeamsByUserGuid(@PathVariable UUID userGuid){
        List<Team> foundTeams = teamService.findAllByUserGuid(userGuid);
        List<TeamDto> dtoFoundTeams = teamMapper.convertToDtoList(foundTeams);
        return ResponseEntity.ok(dtoFoundTeams);
    }


}
