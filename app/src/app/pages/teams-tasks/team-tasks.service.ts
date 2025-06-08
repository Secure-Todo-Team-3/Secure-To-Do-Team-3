import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../shared/services/api.service';

export interface Task {
  id: number;
  title: string;
  description: string;
  dueDate: string;
  status: 'Pending' | 'Completed';
  assignedTo: string;
}

@Injectable({
  providedIn: 'root'
})
export class TeamTasksService {
  private readonly endpoint = '/tasks';

  constructor(private api: ApiService) {}

  getTasks(): Observable<Task[]> {
    return this.api.get<Task[]>(this.endpoint);
  }

  markComplete(taskId: number): Observable<Task> {
    return this.api.patch<Task>(`${this.endpoint}/${taskId}/mark-complete`, {});
  }

  removeTask(taskId: number): Observable<void> {
    return this.api.delete<void>(`${this.endpoint}/${taskId}`);
  }

  toggleAssignment(taskId: number, user: string): Observable<Task> {
    return this.api.patch<Task>(`${this.endpoint}/${taskId}/toggle-assignment`, { user });
  }
}