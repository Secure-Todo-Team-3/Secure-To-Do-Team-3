import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { TeamCardComponent } from 'src/app/shared/components/team-card/team-card.component';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';
import { Router } from '@angular/router';

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
  ],
  templateUrl: './teams-page.component.html',
  styleUrls: ['./teams-page.component.css'],
})
export class TeamsPageComponent {
  constructor(private router: Router) {}

  myTeams = [
    {
      name: 'Marketing Team Responsible for all marketing initiatives and campaigns',
      description: 'Responsible for all marketing initiatives and campaigns, Responsible for all marketing initiatives and campaigns, Responsible for all marketing initiatives and campaigns, Responsible for all marketing initiatives and campaigns',
      members: 8,
      tasks: 12,
      role: 'Admin',
      isOwner: true,
      id: 'team-123',
    },
    {
      name: 'Finance',
      description: 'Managing budgets and financial planning',
      members: 5,
      tasks: 6,
      role: 'Admin',
      isOwner: true,
      id: 'team-789',
    },
    {
      name: 'Finance',
      description: 'Managing budgets and financial planning',
      members: 5,
      tasks: 6,
      role: 'Admin',
      isOwner: true,
      id: 'team-789',
    },
    {
      name: 'Finance',
      description: 'Managing budgets and financial planning',
      members: 5,
      tasks: 6,
      role: 'Admin',
      isOwner: true,
      id: 'team-789',
    },
  ];

  joinedTeams = [
    {
      name: 'Product Development',
      description: 'Building and improving our core products',
      members: 15,
      tasks: 24,
      role: 'Member',
      isOwner: false,
      id: 'team-456',
    },
    {
      name: 'IT',
      description: 'Managing IT infrastructure and security',
      members: 5,
      tasks: 8,
      role: 'Member',
      isOwner: false,
      id: 'team-123',
    },
    {
      name: 'IT',
      description: 'Managing IT infrastructure and security',
      members: 5,
      tasks: 8,
      role: 'Member',
      isOwner: false,
      id: 'team-123',
    },
    {
      name: 'IT',
      description: 'Managing IT infrastructure and security',
      members: 5,
      tasks: 8,
      role: 'Member',
      isOwner: false,
      id: 'team-123',
    },
  ];

  addTeam() {
    this.router.navigate(['/create-team']);
  }
}
