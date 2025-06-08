import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Location, CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TaskEditService } from './task-edit.service';
import { Task } from '../../shared/models/task.model';
import { environment } from 'src/app/shared/environments/environment';

@Component({
  selector: 'app-task-edit-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './task-edit.component.html',
  styleUrls: ['./task-edit.component.css'],
})
export class TaskEditPageComponent implements OnInit {
  taskForm: FormGroup;
  isEditing = false;
  isLoading = false;
  taskId: string| undefined;
  teams: string[] = [];
  statuses: string[] = [];

  constructor(
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private location: Location,
    private route: ActivatedRoute,
    private router: Router,
    private taskEditService: TaskEditService
  ) {
    this.taskForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      dueDate: ['', Validators.required],
      team: ['', Validators.required],
      assignToMyself: [false],
      status: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state;
    this.teams = state?.['teams'] || [];
    this.statuses = state?.['statuses'] || [];
    if (state?.['task']) {
      this.isEditing = true;
      const task = state['task'] as Task;
      this.taskForm.patchValue({
        title: task.title,
        description: task.description,
        dueDate: task.dueDate,
        team: task.team,
        assignToMyself: task.assignToMyself,
        status: task.status,
      });
      this.taskId = task.id;
    } else if (this.router.url.startsWith('/edit-task')) {
      this.showError('No task data provided for editing.');
      this.isEditing = false;
      this.taskForm.reset({
        title: '',
        description: '',
        dueDate: '',
        team: this.teams.length > 0 ? this.teams[0] : '',
        assignToMyself: false,
        status: this.statuses.length > 0 ? this.statuses[0] : '',
      });
      this.taskId = undefined;
    } else {
      this.isEditing = false;
      this.taskId = undefined;
      this.taskForm.reset({
        title: '',
        description: '',
        dueDate: '',
        team: this.teams.length > 0 ? this.teams[0] : '',
        assignToMyself: false,
        status: this.statuses.length > 0 ? this.statuses[0] : '',
      });
    }
  }

  onSubmit(): void {
    if (this.taskForm.valid) {
      this.isLoading = true;
      const task: Task = {
        ...this.taskForm.value,
        id: this.taskId,
      };
      this.taskEditService.saveTask(task, this.isEditing).subscribe({
        next: () => {
          this.showSuccess(
            this.isEditing
              ? 'Task updated successfully!'
              : 'Task created successfully!'
          );
          this.router.navigate(['/tasks']);
          this.isLoading = false;
        },
        error: () => {
          this.showSuccess('Failed to save task.');
          this.isLoading = false;
        },
      });
    } else {
      this.taskForm.markAllAsTouched();
    }
  }

  onCancel(): void {
    this.location.back();
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: environment.snackbarDuration,
      panelClass: ['error-snackbar'],
    });
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: environment.snackbarDuration,
      panelClass: ['success-snackbar'],
    });
  }
}
