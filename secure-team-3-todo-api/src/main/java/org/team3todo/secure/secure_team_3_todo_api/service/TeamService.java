package org.team3todo.secure.secure_team_3_todo_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.mapper.TeamMapper;
import org.team3todo.secure.secure_team_3_todo_api.repository.TeamRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserService userService;


    @Autowired
    public TeamService(TeamRepository teamRepository, UserService userService) {
        this.teamRepository = teamRepository;
        this.userService = userService;
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
}
