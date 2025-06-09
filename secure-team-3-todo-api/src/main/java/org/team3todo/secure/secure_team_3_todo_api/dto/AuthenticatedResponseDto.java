package org.team3todo.secure.secure_team_3_todo_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticatedResponseDto {
    private String token;
    private boolean totpRequired;
    private String message;
    
}