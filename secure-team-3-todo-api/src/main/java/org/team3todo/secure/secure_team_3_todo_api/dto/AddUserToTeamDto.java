package org.team3todo.secure.secure_team_3_todo_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddUserToTeamDto {
    @NotBlank(message = "User email cannot be blank.")
    @Email(message = "A valid email address must be provided.")
    @Size(max = 255, message = "Email address cannot exceed 255 characters.")
    private String userEmail;
}
