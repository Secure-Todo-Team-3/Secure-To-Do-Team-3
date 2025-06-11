import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../shared/services/api.service';

export interface UserTaskStatisticDto {
  userGuid: string;
  username: string;
  openTasks: number;
  closedTasks: number;
}

export interface DailyTaskStatisticDto {
  date: string;
  totalOpenForDay: number;
  totalClosedForDay: number;
  userBreakdown: UserTaskStatisticDto[];
  expanded?: boolean;
}

export interface TeamTaskStatisticDto {
  teamId: number;
  teamName: string;
  totalOpenTasks: number;
  totalClosedTasks: number;
  dailyBreakdown: DailyTaskStatisticDto[];
  expanded?: boolean;
}

@Injectable({ providedIn: 'root' })
export class ReportService {
  constructor(private api: ApiService) {}

  getTaskStatistics(days: number = 7): Observable<TeamTaskStatisticDto[]> {
    return this.api.get<TeamTaskStatisticDto[]>('tasks', { days });
  }
}
