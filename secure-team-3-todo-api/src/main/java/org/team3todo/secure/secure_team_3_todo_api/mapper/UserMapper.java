package org.team3todo.secure.secure_team_3_todo_api.mapper;

import org.springframework.stereotype.Component;
import org.team3todo.secure.secure_team_3_todo_api.dto.UserDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
// Assuming Team entity might be used if you uncomment createdTeamIds mapping
// import org.team3todo.secure.secure_team_3_todo_api.entity.Team;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

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
                .build();
    }

    public List<UserDto> convertToDtoList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // You can add a convertToEntity method here if needed for user creation/updates
    /*
     * public User convertToEntity(UserDto userDto) {
     * if (userDto == null) {
     * return null;
     * }
     * return User.builder()
     * // Map relevant fields from DTO to Entity
     * // Be careful with fields like userGuid, createdAt, passwordHash, passwordSalt
     * // as they are typically handled differently (generated or set by specific
     * logic)
     * .username(userDto.getUsername())
     * .email(userDto.getEmail())
     * .isActive(userDto.getIsActive())
     * .isLocked(userDto.getIsLocked())
     * .build();
     * }
     */
}