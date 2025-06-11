package org.team3todo.secure.secure_team_3_todo_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreateRequestDto {

    @NotBlank(message = "Team name cannot be blank.")
    @Size(max = 255, message = "Team name must not exceed 255 characters.")
    private String name;

    @NotBlank(message = "Team description cannot be blank.")
    private String description;
}