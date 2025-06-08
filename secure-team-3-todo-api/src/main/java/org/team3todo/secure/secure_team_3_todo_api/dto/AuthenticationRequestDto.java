package org.team3todo.secure.secure_team_3_todo_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequestDto {
    private String username;
    private String email;
    private String password;
}
