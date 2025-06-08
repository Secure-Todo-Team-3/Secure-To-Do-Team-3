import { Component, OnInit, signal } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { TeamEditService } from './team-edit.service';
import { environment } from 'src/app/shared/environments/environment';
import { Team } from 'src/app/shared/models/team.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-team-edit',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDividerModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    FormsModule
  ],
  templateUrl: './team-edit.component.html',
  styleUrls: ['./team-edit.component.css']
})
export class TeamEditComponent implements OnInit {
  isEditMode = signal(false);
  isLoading = false;
  teamForm!: FormGroup;
  teamId: string | undefined;

  constructor(
    private router: Router,
    private location: Location,
    private fb: FormBuilder,
    private teamEditService: TeamEditService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.teamForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required]
    });

    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state;

    if (state?.['team']) {
      this.isEditMode.set(true);
      const team = state['team'] as Team;
      this.teamForm.patchValue({
        name: team.name,
        description: team.description
      });
      this.teamId = team.id;
    } else if (this.router.url.startsWith('/edit-team')) {
      this.showError('No team data provided for editing.');
    } else {
      this.isEditMode.set(false);
      this.teamId = undefined;
      this.teamForm.reset();
    }
  }

  goBack() {
    this.router.navigate(['/teams']);
  }

  cancelChanges() {
    this.location.back();
  }

  saveChanges() {
    if (this.teamForm.invalid) return;

    this.isLoading = true;
    const formData = this.teamForm.value;

    const updatedTeam: Team = {
      id: this.teamId || '',
      name: formData.name,
      description: formData.description,
      isOwner: false,
      members: [],
      tasks: [],
      status: ''
    };

    this.teamEditService.saveTeam(updatedTeam, this.isEditMode()).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/teams']);
      },
      error: (error) => {
        this.isLoading = false;
        this.showError('Failed to save team: ' + (error.message || 'Unknown error'));
      }
    });
  }

  private showError(message: string) {
    this.snackBar.open(message, 'Close', {
      duration: environment.snackbarDuration,
      panelClass: ['snackbar-error']
    });
  }
}
