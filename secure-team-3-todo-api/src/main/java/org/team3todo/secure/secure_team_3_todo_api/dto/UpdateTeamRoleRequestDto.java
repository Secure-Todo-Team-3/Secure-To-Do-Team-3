package org.team3todo.secure.secure_team_3_todo_api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateTeamRoleRequestDto {
    private Long newRoleId;
}