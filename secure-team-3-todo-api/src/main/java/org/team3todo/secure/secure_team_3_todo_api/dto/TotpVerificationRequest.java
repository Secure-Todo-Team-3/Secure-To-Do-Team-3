package org.team3todo.secure.secure_team_3_todo_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotpVerificationRequest {

    @NotBlank(message = "Username cannot be blank.")
    @Size(max = 255, message = "Username cannot exceed 255 characters.")
    private String username;

    @NotBlank(message = "Code cannot be blank.")
    @Size(min = 6, max = 6, message = "TOTP code must be 6 digits.")
    private String code;
}