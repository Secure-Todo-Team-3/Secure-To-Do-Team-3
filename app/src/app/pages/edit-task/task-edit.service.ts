import { Injectable } from '@angular/core';
import { ApiService } from '../../shared/services/api.service';
import { Observable } from 'rxjs';
import { Task } from 'src/app/shared/models/task.model';
import { Status } from 'src/app/shared/models/status.model';
import { Team } from 'src/app/shared/models/team.model';

@Injectable({ providedIn: 'root' })
export class TaskEditService {
  constructor(private api: ApiService) {}
  
  saveTask(task: Task, isEditing: boolean): Observable<Task> {
    return isEditing
      ? this.api.put<Task>(`/tasks/${task.taskGuid}`, task)
      : this.api.post<Task>(`task`, task);
  }

  loadTask(id: string): Observable<Task> {
    return this.api.get<Task>(`task/${id}`);
  }

  loadStatuses(): Observable<Status[]> {
    return this.api.get<Status[]>('task-status');
  }

  loadUserTeams(): Observable<Team[]> {
    return this.api.get<Team[]>('team/user-teams', { type: 'member' });
  }


}
