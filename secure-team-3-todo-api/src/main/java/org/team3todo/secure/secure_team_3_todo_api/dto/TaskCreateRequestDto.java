package org.team3todo.secure.secure_team_3_todo_api.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequestDto {

    private String name;
    private String description;
    private OffsetDateTime dueDate;
    private Long teamId;
    private UUID assignedToUserGuid;

}