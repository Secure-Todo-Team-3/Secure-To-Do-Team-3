package org.team3todo.secure.secure_team_3_todo_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {
    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    // Optional: for validation errors
    private Map<String, String> validationErrors;
}