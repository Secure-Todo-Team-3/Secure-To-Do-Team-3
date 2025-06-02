package org.team3todo.secure.secure_team_3_todo_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.service.TeamService;
import org.team3todo.secure.secure_team_3_todo_api.service.UserService;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/team")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDto> getTeamByGUID(@PathVariable Long teamId){
        TeamDto foundTeam = teamService.findById(teamId);
        if(foundTeam != null){
            return ResponseEntity.ok(foundTeam);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
}
