package org.team3todo.secure.secure_team_3_todo_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.TeamMembership;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
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
        Optional<Team> team = teamRepository.findById(id);
        return team.orElse(null);
    }

    public List<Team> findAllByUserGuid(UUID userGuid){
        User user =  userService.findByUserGuid(userGuid);
        if (user != null){
            List<Team> teams = teamRepository.findByTeamMemberships_User(user);
            return teams;
        }
        else{
            return Collections.emptyList();
        }
    }

    public List<User> getUsersInATeam(Long teamId){
        Team foundTeam = findById(teamId);
        if( foundTeam != null ){
            List<TeamMembership> memberships = teamMembershipRepository.findByTeamId(foundTeam);
            List<User> users = memberships.stream().map(TeamMembership::getUser).toList();
            return users;
        }
        else { // should throw an exception.
            return Collections.emptyList();
        }
    }
    //TODO: Remove magic number. Maybe make this more flexible to select by role id lol
    public List<Team> findTeamsWhereUserIsMemberByGuid(UUID userGuid) {
        User user = userService.findByUserGuid(userGuid);
        if (user == null) return Collections.emptyList();
        ArrayList<Long> roleIdsToExclude = new ArrayList<Long>();
        roleIdsToExclude.add(1L);
        roleIdsToExclude.add(2L);
        return teamRepository.findByTeamMemberships_UserAndTeamMemberships_Role_IdNotIn(user, roleIdsToExclude); // Assuming this method exists in TeamRepository
    }

    public List<Team> findTeamsCreatedByUserGuid(UUID userGuid) {
        User user = userService.findByUserGuid(userGuid);
        if (user == null) return Collections.emptyList();
        return teamRepository.findByCreatedByUserId(user); // Assuming this method exists in TeamRepository
        // and Team.createdByUserId is of type User
    }

    public TeamMembership addUserToTeam(String userEmail, Long teamId){
        User user = userService.findByUserEmail(userEmail);
        if (user != null){
            TeamMembership membership = teamMembershipService.addUserToTeam(user.getId(), teamId, 3L);
            return membership;
        }
        else{
            return null;
        }
    }
}
