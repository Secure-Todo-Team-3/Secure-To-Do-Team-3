import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
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
    return this.api.get<Team[]>('team/user-teams', { type: 'member' });
  }

}