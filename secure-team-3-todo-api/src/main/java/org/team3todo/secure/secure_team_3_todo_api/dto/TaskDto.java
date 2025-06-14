package org.team3todo.secure.secure_team_3_todo_api.dto; // Or your preferred DTO package

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    private UUID taskGuid; // The task's public unique identifier

    private String name;
    private String description;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime dueDate;

    // Identifiers for related entities
    private Long teamId;
    private String teamName; // Optional: if you want to include the team's name directly

    private UUID assignedToUserGuid;
    private String assignedToUsername; // Optional: for easier display

    private UUID creatorUserGuid; // GUID of the user who created the task
    private String creatorUsername; // Optional: for easier display

    private String currentStatusName;
    private Long currentStatusId;

}