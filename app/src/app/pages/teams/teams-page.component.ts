import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { TeamCardComponent } from 'src/app/shared/components/team-card/team-card.component';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';
import { Router } from '@angular/router';
import { TeamsPageService } from './teams-page.service';
import { Team } from 'src/app/shared/models/team.model';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { forkJoin } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AddUserToTeamDialogComponent } from './add-to-team-dialog/add-user-to-team-dialog.component';

@Component({
  selector: 'app-teams-page',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    TeamCardComponent,
    MatProgressSpinnerModule,
    MatTabsModule,
  ],
  templateUrl: './teams-page.component.html',
  styleUrls: ['./teams-page.component.css'],
})
export class TeamsPageComponent implements OnInit {
  myTeams: Team[] = [];
  joinedTeams: Team[] = [];
  isLoading = true;

  constructor(
    private router: Router,
    private teamsPageService: TeamsPageService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadAllTeams();
  }

  loadAllTeams(): void {
    this.isLoading = true;
    forkJoin({
      myTeams: this.teamsPageService.getMyTeams(),
      joinedTeams: this.teamsPageService.getJoinedTeams(),
    }).subscribe({
      next: ({ myTeams, joinedTeams }) => {
        this.myTeams = myTeams;
        this.joinedTeams = joinedTeams;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load teams', err);
        this.snackBar.open('Could not load teams.', 'Close', { duration: 3000 });
        this.isLoading = false;
      },
    });
  }

  addTeam() {
    this.router.navigate(['/create-team']);
  }

   openAddUserDialog(): void {
    if (this.myTeams.length === 0) {
      this.snackBar.open('You must be a lead of a team to add users.', 'Close', { duration: 3000 });
      return;
    }
    const dialogRef = this.dialog.open(AddUserToTeamDialogComponent, {
      width: '25rem',
      data: { teams: this.myTeams, userEmail: null, selectedTeamId: null }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result.userEmail && result.selectedTeamId) {
        this.addUserToTeam(result.userEmail, result.selectedTeamId);
      }
    });
  }

  private addUserToTeam(userEmail: string, teamId: number): void {
    this.teamsPageService.addUserToTeam(userEmail, teamId).subscribe({
      next: () => {
        this.snackBar.open(`Successfully added user to team!`, 'Close', { duration: 3000 });
      },
      error: (err) => {
        console.error('Failed to add user', err);
        const errorMessage = err.error?.message || 'Could not add user. Please check the email and try again.';
        this.snackBar.open(errorMessage, 'Close', { duration: 4000 });
      }
    });
  }
}
