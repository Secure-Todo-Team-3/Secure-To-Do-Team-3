import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule, NgIf } from '@angular/common';

@Component({
  selector: 'app-details-dialog',
  standalone: true,
  imports: [NgIf, MatDialogModule, MatIconModule, CommonModule],
  templateUrl: './details-dialog.component.html',
  styleUrls: ['./details-dialog.component.css'],
})
export class DetailsDialogComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private dialogRef: MatDialogRef<DetailsDialogComponent>
  ) {}

  close() {
    this.dialogRef.close();
  }
}
