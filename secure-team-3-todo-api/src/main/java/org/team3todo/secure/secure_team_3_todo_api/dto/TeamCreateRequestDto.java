package org.team3todo.secure.secure_team_3_todo_api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreateRequestDto {
    private String name;
    private String description;
}
