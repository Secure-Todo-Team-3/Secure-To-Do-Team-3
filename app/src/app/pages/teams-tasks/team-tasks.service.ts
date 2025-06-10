import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../shared/services/api.service';
import { Task } from 'src/app/shared/models/task.model';

@Injectable({
  providedIn: 'root',
})
export class TeamTasksService {
  private readonly endpoint = '/tasks';

  constructor(private api: ApiService) {}

  getTasks(teamId: number): Observable<Task[]> {
    return this.api.get<Task[]>(`task/team-tasks/${teamId}`);
  }

  markComplete(taskId: number): Observable<Task> {
    return this.api.post<Task>(`${this.endpoint}/${taskId}/mark-complete`, {});
  }

  removeTask(taskId: number): Observable<void> {
    return this.api.delete<void>(`${this.endpoint}/${taskId}`);
  }

  toggleAssignment(taskId: number, user: string): Observable<Task> {
    return this.api.post<Task>(`${this.endpoint}/${taskId}/toggle-assignment`, {
      user,
    });
  }
}
