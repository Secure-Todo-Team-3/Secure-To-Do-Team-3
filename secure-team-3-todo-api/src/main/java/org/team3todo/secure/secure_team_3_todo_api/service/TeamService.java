package org.team3todo.secure.secure_team_3_todo_api.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamCreateRequestDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.TeamMembership;
import org.team3todo.secure.secure_team_3_todo_api.entity.TeamRole;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.exception.DuplicateResourceException;
import org.team3todo.secure.secure_team_3_todo_api.exception.ResourceNotFoundException;
import org.team3todo.secure.secure_team_3_todo_api.repository.TeamMembershipRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.TeamRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.TeamRoleRepository;

import java.util.*;


@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final TeamMembershipRepository teamMembershipRepository;
    private final TeamMembershipService teamMembershipService;
    private final TeamRoleRepository teamRoleRepository;


    @Autowired
    public TeamService(TeamRepository teamRepository, UserService userService, TeamMembershipRepository teamMembershipRepository, TeamMembershipService teamMembershipService, TeamRoleRepository teamRoleRepository) {
        this.teamRepository = teamRepository;
        this.userService = userService;
        this.teamMembershipRepository = teamMembershipRepository;
        this.teamMembershipService = teamMembershipService;
        this.teamRoleRepository = teamRoleRepository;
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

    public List<Team> findTeamsWhereUserIsMemberByGuid(UUID userGuid) {
        List<String> roleNamesToExclude = List.of("Admin", "Team Lead");
        User user = userService.findByUserGuid(userGuid);
        if (user != null) {
            return teamRepository.findByTeamMemberships_UserAndTeamMemberships_TeamRole_NameNotIn(user, roleNamesToExclude);
        }
        else {
            throw new ResourceNotFoundException("User with GUID: " + userGuid + " does not exist.");
        }
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

    @Transactional
    public Team createTeam(TeamCreateRequestDto teamRequest, User creator) {
        if (teamRepository.existsByName(teamRequest.getName())) {
            throw new DuplicateResourceException("A team with the name '" + teamRequest.getName() + "' already exists.");
        }

        Team newTeam = Team.builder()
                .name(teamRequest.getName())
                .description(teamRequest.getDescription())
                .createdByUserId(creator)
                .isActive(true)
                .build();

        Team savedTeam = teamRepository.save(newTeam);
        TeamRole adminRole = teamRoleRepository.findByName("Admin")
                .orElseThrow(() -> new IllegalStateException("Default 'Admin' role not found in database."));
        teamMembershipService.addUserToTeam(creator.getId(), savedTeam.getId(), adminRole.getId());

        return savedTeam;
    }
}
