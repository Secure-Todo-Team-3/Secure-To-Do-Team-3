import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Location } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-team-edit',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDividerModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './team-edit.component.html',
  styleUrls: ['./team-edit.component.css']
})
export class TeamEditComponent implements OnInit {
  isEditMode = false;
  isLoading = false;
  team = {
    name: '',
    description: '',
    status: 'Active'
  };
  originalTeamData: any;

  constructor(
    private location: Location,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.isEditMode = this.router.url.includes('edit-team');
    this.isLoading = true;
    
    setTimeout(() => {
      if (this.isEditMode) {
        this.loadTeamData();
      }
      this.isLoading = false;
    }, 500);
  }

  loadTeamData() {
    // Simulate API call
    this.team = {
      name: 'Marketing Form',
      description: 'Assurance for all marketing initiatives and campaigns',
      status: 'Active'
    };
    this.originalTeamData = { ...this.team };
  }

  goBack() {
    this.router.navigate(['/teams']);
  }

  cancelChanges() {
    if (this.isEditMode) {
      this.team = { ...this.originalTeamData };
    } else {
      this.team = { name: '', description: '', status: 'Active' };
    }
  }

  saveChanges() {
    this.isLoading = true;
    setTimeout(() => {
      if (this.isEditMode) {
        console.log('Updating team:', this.team);
      } else {
        console.log('Creating team:', this.team);
      }
      this.isLoading = false;
      this.router.navigate(['/teams']);
    }, 1000);
  }
}