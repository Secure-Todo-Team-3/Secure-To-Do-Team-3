package org.team3todo.secure.secure_team_3_todo_api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateRequestDto {

    @Size(max = 255, message = "Task name cannot exceed 255 characters.")
    private String name;

    private String description;

    @FutureOrPresent(message = "Due date must be in the present or in the future.")
    private OffsetDateTime dueDate;

    @Positive(message = "Status ID must be a positive number.")
    private Long currentStatusId;
}