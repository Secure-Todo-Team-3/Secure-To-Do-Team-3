import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { ReportService, TeamTaskStatisticDto, DailyTaskStatisticDto, UserTaskStatisticDto } from './report.service';

@Component({
  selector: 'app-report',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatTableModule,
    MatIconModule,
  ],
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.css'],
})
export class ReportComponent implements OnInit {
  timePeriod = 'day';
  numberOfDays = 2;
  teams: TeamTaskStatisticDto[] = [];
  isLoading = false;

  constructor(private reportService: ReportService) {}

  ngOnInit(): void {
    this.loadStatistics();
  }

  loadStatistics() {
    this.isLoading = true;
    this.reportService.getTaskStatistics(this.numberOfDays).subscribe({
      next: (teams) => {
        this.teams = teams.map(team => ({
          ...team,
          expanded: false,
          dailyBreakdown: team.dailyBreakdown.map(date => ({
            ...date,
            expanded: false
          }))
        }));
        this.isLoading = false;
      },
      error: () => {
        this.teams = [];
        this.isLoading = false;
      }
    });
  }

  toggleTeam(team: TeamTaskStatisticDto) {
    team.expanded = !team.expanded;
  }

  toggleDate(date: DailyTaskStatisticDto) {
    date.expanded = !date.expanded;
  }

  applyFilters() {
    this.loadStatistics();
  }

  resetFilters() {
    this.timePeriod = 'day';
    this.numberOfDays = 2;
    this.loadStatistics();
  }

  logTasks(context: string) {
    console.log('View Tasks clicked for:', context);
  }

  getTeamOpen(team: TeamTaskStatisticDto): number {
    return team.totalOpenTasks;
  }

  getTeamClosed(team: TeamTaskStatisticDto): number {
    return team.totalClosedTasks;
  }

  getDateOpen(date: DailyTaskStatisticDto): number {
    return date.totalOpenForDay;
  }

  getDateClosed(date: DailyTaskStatisticDto): number {
    return date.totalClosedForDay;
  }
}
