import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { DetailsDialogComponent } from '../details-dialog/details-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Team } from '../../models/team.model';

@Component({
  selector: 'app-team-card',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, MatButtonModule],
  templateUrl: './team-card.component.html',
  styleUrls: ['./team-card.component.css'],
})
export class TeamCardComponent {
  @Input() team!: Team;
  constructor(private dialog: MatDialog, private router: Router) {}

  openDetails() {
    this.dialog.open(DetailsDialogComponent, {
      data: {
        ...this.team,
        type: 'team',
      },
      width: '400px',
    });
  }

  editTeam() {
    this.router.navigate(['/edit-team', this.team.id], {
      state: { team: this.team },
    });
  }

  openMembers() {
    this.router.navigate(['/team-members', this.team.id]);
  }

  openTasks() {
    this.router.navigate(['/team-tasks', this.team.id]);
  }

}
