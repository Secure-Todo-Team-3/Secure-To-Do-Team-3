package org.team3todo.secure.secure_team_3_todo_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamMembershipDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.UserDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.TeamMembership;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.mapper.TeamMapper;
import org.team3todo.secure.secure_team_3_todo_api.mapper.TeamMembershipMapper;
import org.team3todo.secure.secure_team_3_todo_api.mapper.UserMapper;
import org.team3todo.secure.secure_team_3_todo_api.service.TeamMembershipService;
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
    private final UserMapper userMapper;
    private final TeamMembershipMapper teamMembershipMapper;

    @Autowired
    public TeamController(TeamService teamService, TeamMapper teamMapper, UserMapper userMapper, TeamMembershipMapper teamMembershipMapper) {
        this.teamService = teamService; this.teamMapper = teamMapper; this.userMapper = userMapper; this.teamMembershipMapper = teamMembershipMapper;
    }
    //Get the team by its ID
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
    // Get the teams of a user where they are not an owner.
    @GetMapping(value = "/user-teams/{userGuid}", params = "type=member")
    public ResponseEntity<List<TeamDto>> getTeamsUserIsMemberOf(@PathVariable UUID userGuid){
        List<Team> foundTeams = teamService.findTeamsWhereUserIsMemberByGuid(userGuid);
        List<TeamDto> dtoFoundTeams = teamMapper.convertToDtoList(foundTeams);
        return ResponseEntity.ok(dtoFoundTeams);
    }

    // Get the teams of a user where they are an owner
    @GetMapping(value = "/user-teams/{userGuid}", params = "type=team_lead")
    public ResponseEntity<List<TeamDto>> getTeamsUserIsLeaderOf(@PathVariable UUID userGuid){
        List<Team> foundTeams = teamService.findTeamsCreatedByUserGuid(userGuid);
        List<TeamDto> dtoFoundTeams = teamMapper.convertToDtoList(foundTeams);
        return ResponseEntity.ok(dtoFoundTeams);
    }

    // Get the users of a team.
    @GetMapping("/{teamId}/users")
    public ResponseEntity<List<UserDto>> getUsersInATeam(@PathVariable Long teamId){
        List<User> foundUsers = teamService.getUsersInATeam(teamId);
        List<UserDto> dtoFoundUsers = userMapper.convertToDtoList(foundUsers);
        return ResponseEntity.ok(dtoFoundUsers);
    }

    @PostMapping("/{teamId}/add-user")
    public ResponseEntity<TeamMembershipDto> addUserToteam(@RequestBody String userEmail, @PathVariable Long teamId){
        TeamMembership returnedTeamMembership = teamService.addUserToTeam(userEmail, teamId);
        if(returnedTeamMembership != null){
            TeamMembershipDto dtoReturnedTeam = teamMembershipMapper.convertToDto(returnedTeamMembership);
            return ResponseEntity.ok(dtoReturnedTeam);
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }


}
