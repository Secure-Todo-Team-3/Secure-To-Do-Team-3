package org.team3todo.secure.secure_team_3_todo_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.dto.UserDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService{
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUserGuid(UUID guid) {
        Optional<User> user = userRepository.findByUserGuid(guid);
        return user.get();
    }

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
