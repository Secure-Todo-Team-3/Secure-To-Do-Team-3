package org.team3todo.secure.secure_team_3_todo_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.team3todo.secure.secure_team_3_todo_api.validation.ValidPassword;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequestDto {
    @Size(max = 255, message = "Username cannot exceed 255 characters.")
    private String username;
    @Email(message = "If provided, email must be in a valid format.")
    @Size(max = 255, message = "Email cannot exceed 255 characters.")
    private String email;
    private String password;
}
