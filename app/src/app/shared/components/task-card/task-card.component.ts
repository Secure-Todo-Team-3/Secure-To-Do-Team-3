import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CommonModule, NgIf } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { DetailsDialogComponent } from '../details-dialog/details-dialog.component';
import { Router } from '@angular/router';

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
  @Input() task: any;
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
    this.router.navigate(['/edit-task', this.task.id]);
  }

  get statusColor(): string {
    switch (this.task.status.toLowerCase()) {
      case 'pending':
        return 'warn';
      case 'completed':
        return 'primary';
      case 'in progress':
        return 'accent';
      default:
        return '';
    }
  }
}
