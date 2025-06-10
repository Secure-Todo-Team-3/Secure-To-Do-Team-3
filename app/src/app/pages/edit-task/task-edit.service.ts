import { Injectable } from '@angular/core';
import { ApiService } from '../../shared/services/api.service';
import { forkJoin, map, Observable } from 'rxjs';
import { Task } from 'src/app/shared/models/task.model';
import { Status } from 'src/app/shared/models/status.model';
import { Team } from 'src/app/shared/models/team.model';

@Injectable({ providedIn: 'root' })
export class TaskEditService {
  constructor(private api: ApiService) {}

  saveTask(task: Task,isEditing: boolean, oldTask?: Task): Observable<Task> {

    if (isEditing) {
      return this.api.post<Task>(`task/${task.taskGuid}/update`, {
        ...(oldTask?.name !== task.name && { name: task.name }),
        ...(oldTask?.description !== task.description && { description: task.description }),
        ...(oldTask?.currentStatusId !== task.currentStatusId && { currentStatusId: task.currentStatusId }),
      })
    } else {
      return   this.api.post<Task>('task', task);
    }

  }

  loadTask(id: string): Observable<Task> {
    return this.api.get<Task>(`task/${id}`);
  }

  loadStatuses(): Observable<Status[]> {
    return this.api.get<Status[]>('task-status');
  }

  loadUserTeams(): Observable<Team[]> {
    return forkJoin([
      this.api.get<Team[]>('team/user-teams', { type: 'member' }),
      this.api.get<Team[]>('team/user-teams', { type: 'team_lead' }),
    ]).pipe(
      map(([memberTeams, managerTeams]) => [...memberTeams, ...managerTeams])
    );
  }
}
