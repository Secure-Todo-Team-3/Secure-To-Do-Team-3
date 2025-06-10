import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CommonModule, NgIf } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { DetailsDialogComponent } from '../details-dialog/details-dialog.component';
import { Router } from '@angular/router';
import { Task } from '../../models/task.model';

@Component({
  selector: 'app-task-card',
  standalone: true,
  imports: [
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatTooltipModule,
    NgIf,
    CommonModule
  ],
  templateUrl: './task-card.component.html',
  styleUrls: ['./task-card.component.css'],
})
export class TaskCardComponent {
  @Input() task!: Task;
  @Output() unassigned = new EventEmitter<any>();

  constructor(private dialog: MatDialog, private router: Router) {}

  openDetails() {
    this.dialog.open(DetailsDialogComponent, {
      data: {
        ...this.task,
        type: 'task',
      },
      width: '400px',
    });
  }

  unassign() {
    this.unassigned.emit(this.task);
  }

  editTask() {
    console.log('Editing task:', this.task);
    this.router.navigate(['/edit-task', this.task.taskGuid]);
  }

  getstatusColor(status: String): string {
    const completedStatuses: String[] = ['Completed', 'Done', 'Closed'];
    const pendingStatuses: String[] = ['Pending', 'In Progress', 'On Hold'];
    if (completedStatuses.includes(status)) {
      return 'green';
    } else if (pendingStatuses.includes(status)) {
      return 'orange';
    } else {
      return 'gray';
    }
  }
}
