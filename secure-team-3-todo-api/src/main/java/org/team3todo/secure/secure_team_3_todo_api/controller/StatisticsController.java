package org.team3todo.secure.secure_team_3_todo_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamTaskStatisticDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.service.StatisticsService;
import org.team3todo.secure.secure_team_3_todo_api.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final UserService userService;

    public StatisticsController(StatisticsService statisticsService, UserService userService) {
        this.statisticsService = statisticsService;
        this.userService = userService;
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TeamTaskStatisticDto>> getTaskStatistics(
            @RequestParam(name = "days", defaultValue = "7") int numberOfDays,
            Authentication authentication) {

        UUID teamLeadGuid = (UUID) authentication.getPrincipal();
        User teamLead = userService.findByUserGuid(teamLeadGuid);

        List<TeamTaskStatisticDto> statistics = statisticsService.getTaskStatisticsForTeamLead(teamLead, numberOfDays);

        return ResponseEntity.ok(statistics);
    }
}