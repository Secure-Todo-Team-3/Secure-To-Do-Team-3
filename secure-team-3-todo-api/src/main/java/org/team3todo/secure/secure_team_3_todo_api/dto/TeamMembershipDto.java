package org.team3todo.secure.secure_team_3_todo_api.dto; // Or your preferred DTO package

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID; // Assuming User has a userGuid

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMembershipDto {

    private Long id; // ID of the TeamMembership record itself

    // User details
    private UUID userGuid;
    private String username;

    // Team details
    private Long teamId;
    private String teamName;

    // Role details
    private Long roleId; // Assuming Role ID is Integer based on SERIAL
    private String roleName;

    private OffsetDateTime assignedAt;
}