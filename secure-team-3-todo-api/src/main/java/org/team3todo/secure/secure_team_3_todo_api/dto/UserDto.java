package org.team3todo.secure.secure_team_3_todo_api.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private UUID userGuid;
    private String username;
    private String email;
    private Boolean isActive;
    private Boolean isLocked;
    private OffsetDateTime createdAt;
    private String systemRole;
    private List<TeamRoleInfo> teamRole;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamRoleInfo {
        private String teamName;
        private String roleName;
    }
}