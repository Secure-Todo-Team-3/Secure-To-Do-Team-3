import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { DetailsDialogComponent } from '../details-dialog/details-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-team-card',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, MatButtonModule],
  templateUrl: './team-card.component.html',
  styleUrls: ['./team-card.component.css'],
})
export class TeamCardComponent {
  @Input() team: any;
  constructor(private dialog: MatDialog) {}

  openDetails() {
    this.dialog.open(DetailsDialogComponent, {
      data: {
        ...this.team,
        type: 'team',
      },
      width: '400px',
    });
  }
}
