import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { map, startWith } from 'rxjs/operators';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

import { getInitials } from '../../shared/utils/get-initials';
import { User } from 'src/app/shared/models/user.model';
import { TeamService } from './team-members.service';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { forkJoin } from 'rxjs';
import { environment } from 'src/app/shared/environments/environment';

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
    MatInputModule,
  ],
  templateUrl: './team-members.component.html',
  styleUrls: ['./team-members.component.css'],
})
export class TeamMembersComponent implements OnInit {
  teamMembers: User[] = [];
  allUsers: User[] = [];
  searchControl = new FormControl();
  filteredUsers = this.searchControl.valueChanges.pipe(
    startWith(''),
    map(value => this._filterUsers(value.username))
  );

  newMemberEmail: string | undefined;
  teamId: number | undefined;
  getInitials = getInitials;

  constructor(
    private snackBar: MatSnackBar,
    private router: Router,
    private teamService: TeamService
  ) {}

  ngOnInit(): void {
    this.loadInitialData();
  }

  private loadInitialData() {
    const teamId: number = Number(this.router.url.split('/').pop());
    this.teamId = teamId;
    forkJoin([
      this.teamService.getTeamMembers(teamId),
      this.teamService.getAllUsers(),
      this.teamService.getMe()
    ]).subscribe({
      next: ([members, users, me]) => {
        this.teamMembers = members.map(member => ({
          ...member,
          role: member.username === me.username ? {name: 'Admin'} : {name: 'Member'}
        } as User));

        this.allUsers = users;

        this.searchControl.setValue('');
      },
      error: (error) => {
        this.snackBar.open('Failed to load team members: ' + (error.message || 'Unknown error'), 'Close', { duration: environment.snackbarDuration});
      }
    });
  }

  private _filterUsers(value: string): User[] {
  const filterValue = (value || '').toLowerCase();
  return this.allUsers.filter(user =>
    !this.teamMembers.some(member => member.username === user.username) &&
    user.username.toLowerCase().includes(filterValue)
  );
}


  goBack() {
    this.router.navigate(['/teams']);
  }

  resetForm() {
    this.newMemberEmail = undefined;
    this.searchControl.setValue('');
  }

  displayUser(user: User): string {
    return user && user.username
  }

  selectUser(user: User) {
    this.newMemberEmail = user.email;
  }

  addMember() {
    this.teamService.addMember(this.teamId!, this.newMemberEmail!).subscribe((newMember) => {
      this.teamMembers.push({ ...newMember, role: { name: 'Member' } } as User);
      this.snackBar.open(`${newMember.username} added to team`, 'Close', { duration: environment.snackbarDuration });
      this.resetForm();
    });
  }

}
