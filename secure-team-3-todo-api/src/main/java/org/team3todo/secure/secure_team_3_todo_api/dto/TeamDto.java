package org.team3todo.secure.secure_team_3_todo_api.dto; // Or your DTO package

import lombok.Data;
import lombok.Builder;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID; // Assuming userGuid is what you want to show

@Data
@Builder
public class TeamDto {
    private Long id;
    private String name;
    private String description;
    private UUID createdByUserId; // Or Long for user ID, or String for username
    private String createdByUsername; // Example: if you want the username
    private Boolean isActive;
    private OffsetDateTime createdAt;
    // You might also want DTOs for teamMemberships and tasks if you include them
    // private List<TeamMembershipDto> teamMemberships;
    // private List<TaskDto> tasks;
}