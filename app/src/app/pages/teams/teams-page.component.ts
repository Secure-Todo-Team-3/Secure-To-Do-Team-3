import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { TeamCardComponent } from 'src/app/shared/components/team-card/team-card.component';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';

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
  myTeams = [
    {
      name: 'Marketing Team',
      description: 'Responsible for all marketing initiatives and campaigns',
      members: 8,
      tasks: 12,
      role: 'Admin',
      isOwner: true,
    },
    {
      name: 'Finance',
      description: 'Managing budgets and financial planning',
      members: 5,
      tasks: 6,
      role: 'Admin',
      isOwner: true,
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
    },
    {
      name: 'IT',
      description: 'Managing IT infrastructure and security',
      members: 5,
      tasks: 8,
      role: 'Member',
      isOwner: false,
    },
  ];
}
