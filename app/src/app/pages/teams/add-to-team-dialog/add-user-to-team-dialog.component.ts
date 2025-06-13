/* add-user-to-team-dialog.component.ts
  This component now manages adding a NEW USER to a team by their email.
*/
import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { Team } from 'src/app/shared/models/team.model';

@Component({
  selector: 'app-add-user-to-team-dialog', // Renamed for clarity
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule
  ],
  templateUrl: "./add-user-to-team-dialog.component.html",
  styleUrls: ["./add-user-to-team-dialog.component.css"]
})
export class AddUserToTeamDialogComponent {
  
    constructor(
    public dialogRef: MatDialogRef<AddUserToTeamDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { 
        teams: Team[], 
        userEmail: string | null, 
        selectedTeamId: number | null 
    }
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  onAddClick(): void {
    // Return the data object which now contains the selected teamId and the email.
    if (this.data.userEmail && this.data.selectedTeamId) {
      this.dialogRef.close(this.data);
    }
  }
}