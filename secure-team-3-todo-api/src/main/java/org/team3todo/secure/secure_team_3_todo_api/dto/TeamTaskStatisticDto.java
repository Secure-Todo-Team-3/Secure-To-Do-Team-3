package org.team3todo.secure.secure_team_3_todo_api.dto;

import java.util.List;
import lombok.Data;

@Data
public class TeamTaskStatisticDto {
    private Long teamId;
    private String teamName;
    private int totalOpenTasks;
    private int totalClosedTasks;
    private List<DailyTaskStatisticDto> dailyBreakdown;
}
