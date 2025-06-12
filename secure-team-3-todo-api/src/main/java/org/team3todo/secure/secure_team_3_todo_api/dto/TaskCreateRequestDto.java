package org.team3todo.secure.secure_team_3_todo_api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequestDto {

    @NotBlank(message = "Task name cannot be blank.")
    @Size(max = 255, message = "Task name must not exceed 255 characters.")
    private String name;

    @NotBlank(message = "Task description cannot be blank.")
    private String description;

    @NotNull(message = "Due date cannot be null.")
    @FutureOrPresent(message = "Due date must be in the present or in the future.")
    private OffsetDateTime dueDate;

    private Long teamId;
    private UUID assignedToUserGuid;

}