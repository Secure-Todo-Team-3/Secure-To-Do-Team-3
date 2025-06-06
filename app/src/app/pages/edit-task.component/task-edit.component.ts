import { Component, OnInit, signal } from '@angular/core';
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
  isEditing = signal(false);
  taskId = signal<string | null>(null);
  isLoading = signal(false);

  teams = [
    'Marketing Team',
    'Development Team',
    'Design Team',
    'Sales Team',
    'HR Team',
    'Finance Team',
  ];

  constructor(
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private location: Location,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.taskForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      dueDate: ['', Validators.required],
      team: ['', Validators.required],
      assignToMyself: [false],
    });
  }

  ngOnInit(): void {
    const urlSegment = this.route.snapshot.url[0]?.path;

    if (urlSegment === 'edit-task') {
      const id = this.route.snapshot.paramMap.get('id');
      this.taskId.set(id);
      this.isEditing.set(true);
      this.loadTaskData();
    } else {
      this.isEditing.set(false);
    }
  }

  async loadTaskData(): Promise<void> {
    this.isLoading.set(true);
    try {
      // Simulate API call
      await new Promise((resolve) => setTimeout(resolve, 500));

      // Mock data for editing
      this.taskForm.patchValue({
        title: 'Existing Task',
        description: 'This is an existing task description',
        dueDate: '2024-12-31',
        team: 'Marketing Team',
        assignToMyself: false,
      });
    } finally {
      this.isLoading.set(false);
    }
  }

  async onSubmit(): Promise<void> {
    if (this.taskForm.valid) {
      this.isLoading.set(true);
      try {
        // Simulate API call
        await new Promise((resolve) => setTimeout(resolve, 500));

        console.log('Task data:', this.taskForm.value);
        this.showSuccess(
          this.isEditing()
            ? 'Task updated successfully!'
            : 'Task created successfully!'
        );
        this.router.navigate(['/tasks']);
      } finally {
        this.isLoading.set(false);
      }
    } else {
      this.taskForm.markAllAsTouched();
    }
  }

  onCancel(): void {
    this.location.back();
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      panelClass: ['success-snackbar'],
    });
  }
}
