package org.team3todo.secure.secure_team_3_todo_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private UUID userGuid;
    private String username;
    private String email;
    private String systemRole;
    private Boolean isLocked;
    private Boolean isActive;
}