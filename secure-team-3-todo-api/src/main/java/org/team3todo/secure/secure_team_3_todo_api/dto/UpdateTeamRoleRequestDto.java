package org.team3todo.secure.secure_team_3_todo_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateTeamRoleRequestDto {

    @NotNull(message = "A new role ID must be provided.")
    @Positive(message = "Role ID must be a positive number.")
    private Long newRoleId;
}