package org.team3todo.secure.secure_team_3_todo_api.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamCreateRequestDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamUpdateRequestDto;
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
    private final AuditingService auditingService;
    private final PolicyFactory sanitizerPolicy;


    @Autowired
    public TeamService(TeamRepository teamRepository, UserService userService, TeamMembershipRepository teamMembershipRepository, TeamMembershipService teamMembershipService, TeamRoleRepository teamRoleRepository, AuditingService auditingService) { // <-- ADD TO CONSTRUCTOR
        this.teamRepository = teamRepository;
        this.userService = userService;
        this.teamMembershipRepository = teamMembershipRepository;
        this.teamMembershipService = teamMembershipService;
        this.teamRoleRepository = teamRoleRepository;
        this.auditingService = auditingService;
    }

    public Team findById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Team with id: "+id+" does not exist."));
    }

    public Team findByName(String name) {
        return teamRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Team with name: "+name+" does not exist."));
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
        List<TeamMembership> memberships = teamMembershipRepository.findByTeam(foundTeam);
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
        UUID currentUserGuid = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findByUserGuid(currentUserGuid);
        auditingService.setAuditUser(currentUser);

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
        auditingService.setAuditUser(creator);

        String safeName = teamRequest.getName();
        String safeDescription = teamRequest.getDescription();

        if (teamRepository.existsByName(safeName)) {
            throw new DuplicateResourceException("A team with the name '" + safeName + "' already exists.");
        }

        Team newTeam = Team.builder()
                .name(safeName)
                .description(safeDescription)
                .createdByUserId(creator)
                .isActive(true)
                .build();

        Team savedTeam = teamRepository.save(newTeam);
        TeamRole adminRole = teamRoleRepository.findByName("Admin")
                .orElseThrow(() -> new IllegalStateException("Default 'Admin' role not found in database."));
        teamMembershipService.addUserToTeam(creator.getId(), savedTeam.getId(), adminRole.getId());

        return savedTeam;
    }

    public Team updateTeam(Long teamId, TeamUpdateRequestDto teamUpdateRequest) {
        UUID currentUserGuid = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.findByUserGuid(currentUserGuid);
        auditingService.setAuditUser(currentUser);

        Team teamToUpdate = findById(teamId);

        if (currentUser.getId() != teamToUpdate.getCreatedByUserId().getId()) {
            throw new ResourceNotFoundException("You do not have permission to update this team.");
        }

        if (teamUpdateRequest.getName() != null && !teamUpdateRequest.getName().isEmpty()) {
            if (teamRepository.existsByName(teamUpdateRequest.getName())) {
                throw new DuplicateResourceException("A team with the name '" + teamUpdateRequest.getName() + "' already exists.");
            }
            teamToUpdate.setName(teamUpdateRequest.getName());
        }

        if (teamUpdateRequest.getDescription() != null) {
            teamToUpdate.setDescription(teamUpdateRequest.getDescription());
        }

        return teamRepository.save(teamToUpdate);
    }
}