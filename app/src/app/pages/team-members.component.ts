import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormControl } from '@angular/forms';
import { map, startWith } from 'rxjs/operators';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatMenuModule } from '@angular/material/menu';
import { getInitials } from '../shared/utils/get-initials';
import { ConfirmDialogComponent } from '../shared/components/confirm-dialog/confirm-dialog.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-team-members',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatCardModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatDividerModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatAutocompleteModule,
    MatDialogModule,
    MatInputModule,
    MatSnackBarModule,
    MatMenuModule,
  ],
  templateUrl: './team-members.component.html',
  styleUrls: ['./team-members.component.css'],
})
export class TeamMembersComponent {
  teamMembers = [
    { id: 1, name: 'Lisa Johnston', role: 'Marketing Specialist' },
    { id: 2, name: 'Stepko Garcia', role: 'Marketing Team' },
  ];
  getInitials = getInitials;

  availableRoles = [
    'Marketing Specialist',
    'Content Manager',
    'SEO Analyst',
    'Social Media Manager',
    'Team Lead',
  ];

  allUsers = [
    { id: 3, name: 'Jane Gee' },
    { id: 4, name: 'Mike Brown' },
    { id: 5, name: 'Sarah Connor' },
    ...this.teamMembers,
  ];

  searchControl = new FormControl();
  filteredUsers = this.searchControl.valueChanges.pipe(
    startWith(''),
    map((value) => this._filterUsers(value || ''))
  );

  newMember = {
    name: '',
    role: '',
  };

  editingMember: any = null;
  originalRole: string = '';

  constructor(private dialog: MatDialog, private snackBar: MatSnackBar, private router: Router) {}

  private _filterUsers(value: string): any[] {
    const filterValue = value.toLowerCase();
    return this.allUsers.filter(
      (user) =>
        user.name.toLowerCase().includes(filterValue) &&
        !this.teamMembers.some((member) => member.id === user.id)
    );
  }

  goBack() {
    this.router.navigate(['/teams']);
  }

  addMember() {
    if (this.newMember.name && this.newMember.role) {
      const newId = Math.max(...this.allUsers.map((u) => u.id)) + 1;
      const newMember = {
        id: newId,
        ...this.newMember,
      };
      this.teamMembers.push(newMember);
      this.allUsers.push(newMember);
      this.resetForm();
      this.snackBar.open('Member added successfully', 'Close', {
        duration: 3000,
      });
    }
  }

  startEdit(member: any) {
    this.editingMember = { ...member };
  }

  cancelEdit() {
    this.editingMember = null;
  }

  confirmRemove(member: any) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Confirm Removal',
        message: `Are you sure you want to remove ${member.name} from the team?`,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.removeMember(member);
      }
    });
  }

  removeMember(member: any) {
    this.teamMembers = this.teamMembers.filter((m) => m.id !== member.id);
    this.snackBar.open('Member removed', 'Close', { duration: 3000 });
  }

  resetForm() {
    this.newMember = { name: '', role: '' };
    this.searchControl.setValue('');
  }

  selectUser(user: any) {
    this.newMember.name = user.name;
  }

  displayFn(user: any): string {
    return user && user.name ? user.name : '';
  }

  confirmRoleChange() {
    if (this.editingMember.role !== this.originalRole) {
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        width: '350px',
        data: {
          title: 'Confirm Role Change',
          message: `Change ${this.editingMember.name}'s role from ${this.originalRole} to ${this.editingMember.role}?`,
        },
      });

      dialogRef.afterClosed().subscribe((result) => {
        if (result) {
          this.saveEdit();
        } else {
          this.cancelEdit();
        }
      });
    } else {
      this.cancelEdit();
    }
  }

  saveEdit() {
    if (this.editingMember) {
      const index = this.teamMembers.findIndex(
        (m) => m.id === this.editingMember.id
      );
      if (index !== -1) {
        this.teamMembers[index] = { ...this.editingMember };
        this.snackBar.open('Role updated successfully', 'Close', {
          duration: 3000,
        });
      }
      this.cancelEdit();
    }
  }
}
