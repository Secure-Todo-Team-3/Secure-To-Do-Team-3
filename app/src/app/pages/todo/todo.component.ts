import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TaskCardComponent } from 'src/app/shared/components/task-card/task-card.component';
import { Router } from '@angular/router';
import { TodoService } from './todo.service';
import { Task } from 'src/app/shared/models/task.model';

@Component({
  selector: 'app-task-dashboard',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    NgFor,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatCardModule,
    MatChipsModule,
    MatTooltipModule,
    TaskCardComponent,
  ],
  templateUrl: './todo.component.html',
  styleUrls: ['./todo.component.css'],
})
export class TodoPageComponent implements OnInit {
  pendingTasks: Task[] = [];
  completedTasks: Task[] = [];
  isLoading = true;

  constructor(private router: Router, private todoService: TodoService) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.todoService.getPendingTasks().subscribe({
      next: (tasks) => {
        this.pendingTasks = tasks;
        this.isLoading = false;
      },
      error: () => {
        this.pendingTasks = [];
        this.isLoading = false;
      },
    });

    this.todoService.getCompletedTasks().subscribe({
      next: (tasks) => {
        this.completedTasks = tasks;
      },
      error: () => {
        this.completedTasks = [];
      },
    });
  }

  addTask() {
    this.router.navigate(['/create-task']);
  }
}
