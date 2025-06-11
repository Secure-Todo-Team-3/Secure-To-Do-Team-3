package org.team3todo.secure.secure_team_3_todo_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class RegisterRequest {
    @NotBlank(message = "Username cannot be blank.")
    @Size(max = 255, message = "Username cannot exceed 255 characters.")
    private String username;
    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "A valid email address must be provided.")
    @Size(max = 255, message = "Email cannot exceed 255 characters.")
    private String email;
    @NotBlank(message = "Password cannot be blank.")
    @ValidPassword
    private String password;
    @NotBlank(message = "Password confirmation cannot be blank.")
    private String confirmPassword;
    
}
