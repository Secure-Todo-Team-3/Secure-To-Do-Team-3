import { Component } from '@angular/core';
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
export class TodoPageComponent {
  constructor(private router: Router) {}

  pendingTasks: any[] = [
    {
      title: 'Q1 Marketing Campaign. Q1 Marketing Campaign. Q1 Marketing Campaign',
      description: 'Complete project proposal for Q1 marketing campaign',
      dueDate: '2024-01-11',
      status: 'In Progress',
      team: 'Marketing Team',
      chipColor: 'primary',
      icon: 'schedule',
      chipSelected: true,
      id: 'task-456',
    },
    {
      title: 'Q2 Marketing Campaign',
      description: 'Complete project proposal for Q2 marketing campaign',
      dueDate: '2024-01-12',
      status: 'In Progress',
      team: 'Marketing Team',
      chipColor: 'primary',
      icon: 'schedule',
      chipSelected: true,
      id: 'task-123',
    },
    {
      title: 'Q2 Marketing Campaign',
      description: 'Complete project proposal for Q2 marketing campaign',
      dueDate: '2024-01-12',
      status: 'In Progress',
      team: 'Marketing Team',
      chipColor: 'primary',
      icon: 'schedule',
      chipSelected: true,
      id: 'task-123',
    },
  ];

  completedTasks: any[] = [
    {
      title: 'Website Redesign',
      description: 'Complete the company website redesign project',
      dueDate: '2024-01-05',
      status: 'Completed',
      team: 'Marketing Team',
      chipColor: 'accent',
      icon: 'check_circle',
      chipSelected: true,
      id: 'task-789',
    },
    {
      title: 'Annual Report',
      description: 'Prepare annual marketing report for stakeholders',
      dueDate: '2023-12-20',
      status: 'Completed',
      team: 'Marketing Team',
      chipColor: 'accent',
      icon: 'check_circle',
      chipSelected: true,
      id: 'task-456',
    },
    {
      title: 'Team Training',
      description: 'Conduct training session on new marketing tools',
      dueDate: '2023-12-15',
      status: 'Completed',
      team: 'Marketing Team',
      chipColor: 'accent',
      icon: 'check_circle',
      chipSelected: true,
      id: 'task-123',
    },
  ];

  addTask() {
    this.router.navigate(['/create-task']);
  }
}
