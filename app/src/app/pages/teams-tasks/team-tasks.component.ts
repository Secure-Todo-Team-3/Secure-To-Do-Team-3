import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { MatChipsModule } from '@angular/material/chips';
import { Router } from '@angular/router';
import { TeamTasksService } from './team-tasks.service';
import { environment } from 'src/app/shared/environments/environment';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Task } from 'src/app/shared/models/task.model';

@Component({
  selector: 'app-team-tasks',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatListModule,
    MatButtonModule,
    MatDividerModule,
    MatIconModule,
    MatChipsModule,
  ],
  templateUrl: './team-tasks.component.html',
  styleUrls: ['./team-tasks.component.css'],
})
export class TeamTasksComponent {
  tasks: Task[] = [];

  constructor(
    private router: Router,
    private teamTasksService: TeamTasksService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const teamId: number = Number(this.router.url.split('/').pop());
    if (teamId && !isNaN(teamId)) {
      this.teamTasksService.getTasks(teamId).subscribe({
        next: (tasks) => {
          this.tasks = tasks;
          console.log('Tasks loaded:', this.tasks);
        },
        error: () => {
          this.tasks = [];
          this.showError('Failed to load tasks. Please try again later.');
        },
      });
    }
  }

  get openTasksCount(): number {
    return this.tasks.filter((task) => task.currentStatusName === 'Pending')
      .length;
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: environment.snackbarDuration,
      panelClass: ['error-snackbar'],
    });
  }

  addTask(): void {
    this.router.navigate(['/create-task']);
  }

  goBack(): void {
    this.router.navigate(['/tasks']);
  }

  editTask(task: Task): void {
    this.router.navigate(['/edit-task', task.taskGuid], {
      state: { task },
    });
  }

  toggleAssignment(task: Task) {
    throw new Error('Method not implemented.');
  }
  markComplete(task: Task) {
    throw new Error('Method not implemented.');
  }
  removeTask(task: Task) {
    throw new Error('Method not implemented.');
  }
}
