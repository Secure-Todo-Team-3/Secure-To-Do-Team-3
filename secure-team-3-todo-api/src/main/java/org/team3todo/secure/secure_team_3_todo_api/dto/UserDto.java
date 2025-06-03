package org.team3todo.secure.secure_team_3_todo_api.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;
// Import other DTOs if you plan to nest them, e.g., List<TeamSummaryDto>
// For now, we'll keep it simple.

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private UUID userGuid; // Public identifier
    private String username;
    private String email;
    private Boolean isActive;
    private Boolean isLocked;
    private OffsetDateTime createdAt;

    // Example: If you wanted to include IDs of teams created by this user
    // private List<Long> createdTeamIds;

    // Example: If you wanted to include simplified DTOs for team memberships
    // private List<TeamMembershipSummaryDto> teamMemberships;

}