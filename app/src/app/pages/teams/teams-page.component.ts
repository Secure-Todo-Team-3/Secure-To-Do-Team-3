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
    private teamsPageService: TeamsPageService
  ) {}

  ngOnInit(): void {
    this.isLoading = true;
    this.teamsPageService.getMyTeams().subscribe({
      next: (teams) => {
        this.myTeams = teams;
        this.isLoading = false;
      },
      error: () => {
        this.myTeams = [];
        this.isLoading = false;
      }
    });

    this.teamsPageService.getJoinedTeams().subscribe({
      next: (teams) => {
        this.joinedTeams = teams;
      },
      error: () => {
        this.joinedTeams = [];
      }
    });
  }

  addTeam() {
    this.router.navigate(['/create-team']);
  }
}
