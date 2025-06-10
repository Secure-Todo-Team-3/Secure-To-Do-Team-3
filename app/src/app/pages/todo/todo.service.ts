import { Injectable } from '@angular/core';
import { forkJoin, map, Observable } from 'rxjs';
import { ApiService } from '../../shared/services/api.service';
import { Task } from 'src/app/shared/models/task.model';
import { Status } from 'src/app/shared/models/status.model';
import { Team } from 'src/app/shared/models/team.model';

@Injectable({ providedIn: 'root' })
export class TodoService {
  constructor(private api: ApiService) {}

  loadTasks(): Observable<Task[]> {
    return this.api.get<Task[]>('task/user-tasks');
  }

  loadStatuses(): Observable<Status[]> {
    return this.api.get<Status[]>('task-status');
  }

  loadUserTeams(): Observable<Team[]> {
    return forkJoin([
      this.api.get<Team[]>('team/user-teams', { type: 'member' }),
      this.api.get<Team[]>('team/user-teams', { type: 'team_lead' })
    ]).pipe(
      map(([memberTeams, managerTeams]) => [...memberTeams, ...managerTeams])
    );
  }

  searchTasks(
    name?: string,
    status?: string,
    team?: string
  ): Observable<Task[]> {
    const params = {
      ...(name && { name }),
      ...(status && { status }),
      ...(team && { team }),
    };
    return this.api.get<Task[]>('task/user-tasks', params);
  }

}