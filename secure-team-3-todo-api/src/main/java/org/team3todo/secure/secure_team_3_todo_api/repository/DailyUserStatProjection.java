package org.team3todo.secure.secure_team_3_todo_api.repository;

import java.time.LocalDate;
import java.util.UUID;

public interface DailyUserStatProjection {
    LocalDate getReportDate();
    Long getTeamId();
    UUID getUserGuid();
    String getUsername();
    String getStatusCategory();
    Integer getTaskCount();
}