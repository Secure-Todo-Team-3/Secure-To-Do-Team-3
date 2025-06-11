package org.team3todo.secure.secure_team_3_todo_api.service;

import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.dto.DailyTaskStatisticDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamTaskStatisticDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.UserTaskStatisticDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.Team;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.repository.DailyUserStatProjection;
import org.team3todo.secure.secure_team_3_todo_api.repository.TaskStatisticsRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.TeamMembershipRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final TaskStatisticsRepository statisticsRepository;
    private final TeamMembershipRepository teamMembershipRepository;
    private final TeamService teamService;

    public StatisticsService(TaskStatisticsRepository statisticsRepository, TeamMembershipRepository teamMembershipRepository, TeamService teamService) {
        this.statisticsRepository = statisticsRepository;
        this.teamMembershipRepository = teamMembershipRepository;
        this.teamService = teamService;
    }

    public List<TeamTaskStatisticDto> getTaskStatisticsForTeamLead(User teamLead, int numberOfDays) {
        // 1. Find all teams this user is a lead for.
        List<Team> teams = teamMembershipRepository.findTeamsByUserIdAndRoleName(teamLead.getId(), "Team Lead"); // Use the exact role name
        if (teams.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> teamIds = teams.stream().map(Team::getId).toList();
        Map<Long, String> teamNameMap = teams.stream().collect(Collectors.toMap(Team::getId, Team::getName));

        // 2. Calculate start date
        LocalDate startDate = LocalDate.now().minusDays(numberOfDays - 1);

        // 3. Fetch the flat list of raw data from the database
        List<DailyUserStatProjection> rawData = statisticsRepository.getDailyTaskStatistics(teamIds, startDate);

        // 4. Transform the flat data into the required nested structure
        return transformToNestedStructure(rawData, teamNameMap);
    }

    private List<TeamTaskStatisticDto> transformToNestedStructure(List<DailyUserStatProjection> rawData, Map<Long, String> teamNameMap) {
        // Group by Team ID
        Map<Long, List<DailyUserStatProjection>> statsByTeam = rawData.stream()
                .collect(Collectors.groupingBy(DailyUserStatProjection::getTeamId));

        List<TeamTaskStatisticDto> result = new ArrayList<>();

        statsByTeam.forEach((teamId, teamData) -> {
            TeamTaskStatisticDto teamDto = new TeamTaskStatisticDto();
            teamDto.setTeamId(teamId);
            teamDto.setTeamName(teamNameMap.get(teamId));

            // Group this team's data by Date
            Map<LocalDate, List<DailyUserStatProjection>> statsByDate = teamData.stream()
                    .collect(Collectors.groupingBy(DailyUserStatProjection::getReportDate));

            List<DailyTaskStatisticDto> dailyBreakdown = new ArrayList<>();
            statsByDate.forEach((date, dateData) -> {
                DailyTaskStatisticDto dailyDto = new DailyTaskStatisticDto();
                dailyDto.setDate(date);

                // Group this day's data by userGuid
                Map<UUID, List<DailyUserStatProjection>> statsByUser = dateData.stream()
                        .collect(Collectors.groupingBy(DailyUserStatProjection::getUserGuid));

                List<UserTaskStatisticDto> userBreakdown = new ArrayList<>();
                statsByUser.forEach((userGuid, userData) -> {
                    UserTaskStatisticDto userDto = new UserTaskStatisticDto();
                    userDto.setUserGuid(userGuid);
                    userDto.setUsername(userData.get(0).getUsername());

                    int open = userData.stream().filter(s -> "OPEN".equals(s.getStatusCategory())).mapToInt(DailyUserStatProjection::getTaskCount).sum();
                    int closed = userData.stream().filter(s -> "CLOSED".equals(s.getStatusCategory())).mapToInt(DailyUserStatProjection::getTaskCount).sum();

                    userDto.setOpenTasks(open);
                    userDto.setClosedTasks(closed);
                    userBreakdown.add(userDto);
                });

                dailyDto.setUserBreakdown(userBreakdown);
                dailyDto.setTotalOpenForDay(userBreakdown.stream().mapToInt(UserTaskStatisticDto::getOpenTasks).sum());
                dailyDto.setTotalClosedForDay(userBreakdown.stream().mapToInt(UserTaskStatisticDto::getClosedTasks).sum());
                dailyBreakdown.add(dailyDto);
            });

            // Sort daily breakdown by date descending
            dailyBreakdown.sort(Comparator.comparing(DailyTaskStatisticDto::getDate).reversed());

            teamDto.setDailyBreakdown(dailyBreakdown);
            teamDto.setTotalOpenTasks(dailyBreakdown.stream().mapToInt(DailyTaskStatisticDto::getTotalOpenForDay).sum());
            teamDto.setTotalClosedTasks(dailyBreakdown.stream().mapToInt(DailyTaskStatisticDto::getTotalClosedForDay).sum());
            result.add(teamDto);
        });

        result.sort(Comparator.comparing(TeamTaskStatisticDto::getTeamName));
        return result;
    }
}