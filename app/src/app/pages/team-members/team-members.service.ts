import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { ApiService } from 'src/app/shared/services/api.service';
import { User } from 'src/app/shared/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class TeamService {
  constructor(private api: ApiService) {}

  getTeamMembers(teamId: number): Observable<User[]> {
    return this.api.get<User[]>(`team/${teamId}/users`).pipe(
      tap(users => console.log('Team members fetched:', users))
    );
  }

  getMe(): Observable<User> {
    return this.api.get<User>('user/me').pipe(
      tap(user => console.log('Current user fetched:', user))
    );
  }

  getAllUsers(): Observable<User[]> {
    return this.api.get<User[]>('user').pipe(
      tap(users => console.log('All users fetched:', users))
    );
  }

  addMember(teamId: number, userEmail: string): Observable<User> {
    return this.api.post(`team/${teamId}/add-user`, {userEmail});
  }
}
