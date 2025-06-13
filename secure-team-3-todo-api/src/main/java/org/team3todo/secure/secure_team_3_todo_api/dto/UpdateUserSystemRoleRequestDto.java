// src/main/java/org/team3todo/secure/secure_team_3_todo_api/dto/UpdateUserSystemRoleRequestDto.java
package org.team3todo.secure.secure_team_3_todo_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserSystemRoleRequestDto {
    @NotBlank(message = "New system role name is required")
    // IMPORTANT: Adjust this regex to precisely match the actual valid SystemRole names you have in your database.
    // Example: If your SystemRole names are "SYS_ADMIN", "BASIC_USER", "GUEST", adjust accordingly.
    @Pattern(regexp = "^(ADMIN|REGULAR_USER|VIEWER|GUEST)$", message = "Invalid system role name. Must be one of the predefined system roles (e.g., ADMIN, REGULAR_USER, VIEWER, GUEST).")
    private String newSystemRoleName;
}