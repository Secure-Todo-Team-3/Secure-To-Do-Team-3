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

interface Task {
  id: number;
  title: string;
  description: string;
  dueDate: string;
  status: 'Pending' | 'Completed';
  assignedTo: string;
}

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
    MatChipsModule
  ],
  templateUrl: './team-tasks.component.html',
  styleUrls: ['./team-tasks.component.css']
})
export class TeamTasksComponent {
toggleAssignment(_t22: Task) {
throw new Error('Method not implemented.');
}
markComplete(arg0: number) {
throw new Error('Method not implemented.');
}
removeTask(arg0: number) {
throw new Error('Method not implemented.');
}
  tasks: Task[] = [];

  constructor(private router: Router, private teamTasksService: TeamTasksService, private snackBar: MatSnackBar) {}

  ngOnInit(): void {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state as { tasks: Task[] } | undefined;
    if (state && state.tasks) {
      this.tasks = state.tasks;
    } else {
      this.teamTasksService.getTasks().subscribe({
        next: (tasks) => {
          this.tasks = tasks;
        },
        error: () => {
          this.tasks = [];
          this.showError('Failed to load tasks. Please try again later.');
        }   
      });
    }
  }

  get openTasksCount(): number {
    return this.tasks.filter(t => t.status === 'Pending').length;
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: environment.snackbarDuration,
      panelClass: ['error-snackbar']
    });
  }

  addTask(): void {
    this.router.navigate(['/create-task']);
  }

  goBack(): void {
    this.router.navigate(['/teams']);
  }

  editTask(task: Task): void {
    this.router.navigate(['/edit-task', task.id], {
      state: { task }
    });
  }
}
