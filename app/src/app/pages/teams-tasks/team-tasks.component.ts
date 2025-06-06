import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { MatChipsModule } from '@angular/material/chips';
import { Router } from '@angular/router';

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
  currentUser = 'John Doe';

  tasks: Task[] = [
    {
      id: 1,
      title: 'Design Figma mockups',
      description: 'Create layout for the dashboard and user flow screens.',
      dueDate: '2025-06-03',
      status: 'Pending',
      assignedTo: 'John Doe'
    },
    {
      id: 2,
      title: 'Implement authentication',
      description: 'Setup login and registration with JWT.',
      dueDate: '2025-06-10',
      status: 'Pending',
      assignedTo: ''
    },
    {
      id: 3,
      title: 'Update documentation',
      description: 'Revise README and internal API docs.',
      dueDate: '2025-06-05',
      status: 'Completed',
      assignedTo: 'Mike Johnson'
    }
  ];

  constructor(private router: Router) {}

  get openTasksCount(): number {
    return this.tasks.filter(t => t.status === 'Pending').length;
  }

  markComplete(taskId: number): void {
    const task = this.tasks.find(t => t.id === taskId);
    if (task) {
      task.status = 'Completed';
    }
  }

  removeTask(taskId: number): void {
    this.tasks = this.tasks.filter(t => t.id !== taskId);
  }

  addTask(): void {
    window.location.href = '/add-task';
  }

  goBack(): void {
    this.router.navigate(['/']);
  }

  toggleAssignment(task: Task): void {
    task.assignedTo = task.assignedTo === this.currentUser ? '' : this.currentUser;
  }

  editTask(task: Task): void {
    console.log('Edit task:', task);
  }
}
