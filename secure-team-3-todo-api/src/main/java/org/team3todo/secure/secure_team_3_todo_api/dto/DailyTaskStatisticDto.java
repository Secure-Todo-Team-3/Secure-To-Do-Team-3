package org.team3todo.secure.secure_team_3_todo_api.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class DailyTaskStatisticDto {
    private LocalDate date;
    private int totalOpenForDay;
    private int totalClosedForDay;
    private List<UserTaskStatisticDto> userBreakdown;
}
