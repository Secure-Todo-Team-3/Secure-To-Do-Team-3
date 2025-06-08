import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../shared/services/api.service';
import { Task } from 'src/app/shared/models/task.model';

@Injectable({ providedIn: 'root' })
export class TodoService {
  constructor(private api: ApiService) {}

  getPendingTasks(): Observable<Task[]> {
    return this.api.get<Task[]>('/tasks/pending');
  }

  getCompletedTasks(): Observable<Task[]> {
    return this.api.get<Task[]>('/tasks/completed');
  }
}