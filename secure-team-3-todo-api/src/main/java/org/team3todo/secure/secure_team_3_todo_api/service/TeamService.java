package org.team3todo.secure.secure_team_3_todo_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.TeamMembership;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.exception.ResourceNotFoundException;
import org.team3todo.secure.secure_team_3_todo_api.mapper.TeamMapper;
import org.team3todo.secure.secure_team_3_todo_api.repository.TeamMembershipRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.TeamRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final TeamMembershipRepository teamMembershipRepository;
    private final TeamMembershipService teamMembershipService;


    @Autowired
    public TeamService(TeamRepository teamRepository, UserService userService, TeamMembershipRepository teamMembershipRepository, TeamMembershipService teamMembershipService) {
        this.teamRepository = teamRepository;
        this.userService = userService;
        this.teamMembershipRepository = teamMembershipRepository;
        this.teamMembershipService = teamMembershipService;
    }

    public Team findById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Team with id: "+id+" does not exist."));
    }

    public List<Team> findAllByUserGuid(UUID userGuid){
        User user =  userService.findByUserGuid(userGuid);
        if (user != null){
            return teamRepository.findByTeamMemberships_User(user);
        }
        else{
             throw new ResourceNotFoundException("User with GUID: "+userGuid+" does not exist.");
        }
    }

    public List<User> getUsersInATeam(Long teamId){
        Team foundTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + teamId));
        List<TeamMembership> memberships = teamMembershipRepository.findByTeam(foundTeam); // Pass the ID
        return memberships.stream().map(TeamMembership::getUser).toList();
    }

    //TODO: Remove magic number. Maybe make this more flexible to select by role id lol
    public List<Team> findTeamsWhereUserIsMemberByGuid(UUID userGuid, Collection<Long> roleIdsToExclude) {
        User user = userService.findByUserGuid(userGuid);
        if (user != null){
            return teamRepository.findByTeamMemberships_UserAndTeamMemberships_TeamRole_IdNotIn(user, roleIdsToExclude);

        }
        throw new ResourceNotFoundException("User with GUID: "+userGuid+" does not exist.");
    }

    public List<Team> findTeamsCreatedByUserGuid(UUID userGuid) {
        User user = userService.findByUserGuid(userGuid);
        if (user != null) {
            return teamRepository.findByCreatedByUserId(user);
        }
        else {
            throw new ResourceNotFoundException("User with GUID: " + userGuid + " does not exist.");
        }

    }

    public TeamMembership addUserToTeam(String userEmail, Long teamId){
        User user = userService.findByUserEmail(userEmail);
        Team team = findById(teamId);
        if(user == null){
            throw new ResourceNotFoundException("User with email: "+userEmail+" does not exist");
        } else if (team == null) {
            throw new ResourceNotFoundException("Team with id: "+teamId+" does not exist");
        } else{
            return teamMembershipService.addUserToTeam(user.getId(), teamId, 3L);
        }

    }
}
