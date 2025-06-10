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
import { Status } from 'src/app/shared/models/status.model';
import { Team } from 'src/app/shared/models/team.model';
import { forkJoin, merge } from 'rxjs';
import { MatCardActions } from '@angular/material/card';

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
    MatCardActions,
  ],
  templateUrl: './task-edit.component.html',
  styleUrls: ['./task-edit.component.css'],
})
export class TaskEditPageComponent implements OnInit {
  taskForm: FormGroup;
  isEditing = false;
  isLoading = false;
  teams: Team[] = [];
  statuses: Status[] = [];
  maxDueDate: Date = new Date(
    new Date().setFullYear(new Date().getFullYear() + 2)
  );
  minDueDate: Date = new Date(Date.now() + 24 * 60 * 60 * 1000);
  oldTask: Task | undefined;
  constructor(
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private location: Location,
    private router: Router,
    private taskEditService: TaskEditService
  ) {
    this.taskForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      dueDate: ['', Validators.required],
      teamId: ['', Validators.required],
      currentStatusId: ['', Validators.required],
      id: [null],
    });
  }

  ngOnInit(): void {
    if (this.router.url.startsWith('/edit-task')) {
      const taskId: string = this.router.url.split('/').pop() || '';
      forkJoin({
        task: this.taskEditService.loadTask(taskId),
        teams: this.taskEditService.loadUserTeams(),
        statuses: this.taskEditService.loadStatuses(),
      }).subscribe(
        ({ task, teams, statuses }) => {
          this.isEditing = true;
          this.teams = teams;
          this.statuses = statuses;
          this.taskForm.patchValue({
            name: task.name,
            description: task.description,
            dueDate: task.dueDate,
            teamId: task.teamId,
            currentStatusId: task.currentStatusId,
            id: task.taskGuid,
          });
          this.oldTask = { ...task };
        },
        (error) => {
          this.showError(
            'Failed to load task: ' + (error.message || 'Unknown error')
          );
          this.isEditing = false;
          this.taskForm.reset();
          this.teams = [];
          this.statuses = [];
        }
      );
    } else {
      forkJoin({
        teams: this.taskEditService.loadUserTeams(),
        statuses: this.taskEditService.loadStatuses(),
      }).subscribe(({ teams, statuses }) => {
        this.isEditing = false;
        this.teams = teams;
        this.statuses = statuses;
        this.taskForm.reset({
          name: '',
          description: '',
          dueDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000), // Default to one week from now
          teamId: teams.length > 0 ? teams[0].id : '',
          currentStatusId: statuses.length > 0 ? statuses[0].id : '',
          id: null,
        });
        this.oldTask = undefined;
      }, (error) => {
        this.showError(
          'Failed to load teams or statuses: ' + (error.message || 'Unknown error')
        );
        this.teams = [];
        this.statuses = [];
        this.taskForm.reset();
      });
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

  saveChanges() {
    if (this.taskForm.invalid) {
      this.taskForm.markAllAsTouched();
    } else {
      this.isLoading = true;
      const task: Task = {
        ...this.taskForm.value,
        taskGuid: this.isEditing ? this.taskForm.value.id : undefined,
      };

      this.taskEditService.saveTask(task, this.isEditing, this.oldTask).subscribe({
        next: () => {
          this.showSuccess(
            this.isEditing
              ? 'Task updated successfully!'
              : 'Task created successfully!'
          );
          this.router.navigate(['/tasks']);
          this.isLoading = false;
        },
        error: (error) => {
          this.showError(
            'Failed to save task: ' + (error.message || 'Unknown error')
          );
          this.isLoading = false;
        },
      });
    }
  }

  cancelChanges() {
    this.location.back();
  }
}
