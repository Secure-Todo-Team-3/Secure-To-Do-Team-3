package org.team3todo.secure.secure_team_3_todo_api.mapper;

import org.springframework.stereotype.Component;
import org.team3todo.secure.secure_team_3_todo_api.dto.UserDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.TeamMembership;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDto convertToDto(User user) {
    if (user == null) {
        return null;
    }

    List<UserDto.TeamRoleInfo> teamRoles = user.getTeamMemberships().stream()
        .map(membership -> UserDto.TeamRoleInfo.builder()
            .teamName(membership.getTeam().getName())
            .roleName(membership.getTeamRole().getName())
            .build())
        .toList();

    return UserDto.builder()
            .userGuid(user.getUserGuid())
            .username(user.getUsername())
            .email(user.getEmail())
            .isActive(user.getIsActive())
            .isLocked(user.getIsLocked())
            .createdAt(user.getCreatedAt())
            .systemRole(user.getSystemRole().getName())
            .teamRole(teamRoles)
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

}