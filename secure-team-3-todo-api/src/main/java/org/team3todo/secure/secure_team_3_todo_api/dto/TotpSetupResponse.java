package org.team3todo.secure.secure_team_3_todo_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotpSetupResponse {
    private String secret;
    private String qrCodeImageUri;
    private String message;
}