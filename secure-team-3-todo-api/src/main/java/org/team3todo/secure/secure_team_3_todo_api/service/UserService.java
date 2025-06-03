package org.team3todo.secure.secure_team_3_todo_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.dto.UserDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto findByUserGuid(UUID guid) {
        Optional<User> user = userRepository.findByUserGuid(guid);
        return user.map(this::convertToDto).orElse(null);
    }

    // In a service or mapper class
    public UserDto convertToDto(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .userGuid(user.getUserGuid())
                .username(user.getUsername())
                .email(user.getEmail())
                .isActive(user.getIsActive())
                .isLocked(user.getIsLocked())
                .createdAt(user.getCreatedAt())
                // If you decide to include createdTeamIds:
                // .createdTeamIds(user.getCreatedTeams().stream().map(Team::getId).collect(Collectors.toList()))
                .build();
    }
}
