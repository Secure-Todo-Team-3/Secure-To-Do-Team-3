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
import { Status } from 'src/app/shared/models/status.model';
import { Team } from 'src/app/shared/models/team.model';
import { forkJoin } from 'rxjs';
import { MatSpinner } from '@angular/material/progress-spinner';

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
    MatSpinner
  ],
  templateUrl: './todo.component.html',
  styleUrls: ['./todo.component.css'],
})
export class TodoPageComponent implements OnInit {
  tasks: Task[] = [];
  statuses: Status[] = [];
  teams: Team[] = [];
  isLoading = true;
  selectedStatus: string = 'all';
  selectedTeam: string = 'all';
  searchTerm: string = '';

  constructor(private router: Router, private todoService: TodoService) {}

  ngOnInit(): void {
    this.isLoading = true;

    forkJoin({
      tasks: this.todoService.loadTasks(),
      statuses: this.todoService.loadStatuses(),
      teams: this.todoService.loadUserTeams(),
    }).subscribe({
      next: ({ tasks, statuses, teams }) => {
        this.tasks = tasks;
        this.statuses = statuses;
        this.teams = teams;
        this.isLoading = false;
      },
      error: () => {
        this.tasks = [];
        this.statuses = [];
        this.teams = [];
        this.isLoading = false;
      },
    });
  }

  searchTasks() {
    this.isLoading = true;
    this.todoService
      .searchTasks(
        this.searchTerm || undefined,
        this.selectedStatus === 'all' ? undefined : this.selectedStatus,
        this.selectedTeam === 'all' ? undefined : this.selectedTeam,
      )
      .subscribe({
        next: (tasks) => {
          this.tasks = tasks;
          this.isLoading = false;
        },
        error: () => {
          this.tasks = [];
          this.isLoading = false;
        },
      });
  }

  addTask() {
    this.router.navigate(['/create-task']);
  }
}
