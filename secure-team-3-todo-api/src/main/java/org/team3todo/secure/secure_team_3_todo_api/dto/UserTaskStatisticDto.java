package org.team3todo.secure.secure_team_3_todo_api.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class UserTaskStatisticDto {
    private UUID userGuid;
    private String username;
    private int openTasks;
    private int closedTasks;
}