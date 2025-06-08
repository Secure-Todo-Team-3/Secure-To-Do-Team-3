import { Injectable } from '@angular/core';
import { ApiService } from '../../shared/services/api.service';
import { Observable } from 'rxjs';
import { Task } from 'src/app/shared/models/task.model';

@Injectable({ providedIn: 'root' })
export class TaskEditService {
  constructor(private api: ApiService) {}
  
  saveTask(task: Task, isEditing: boolean): Observable<Task> {
    return isEditing
      ? this.api.put<Task>(`/tasks/${task.id}`, task)
      : this.api.post<Task>(`/tasks`, task);
  }
}
