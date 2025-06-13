import { Injectable } from '@angular/core';
import { map, Observable, tap } from 'rxjs';
import { ApiService } from 'src/app/shared/services/api.service';
import { User } from 'src/app/shared/models/user.model';
import { Team } from 'src/app/shared/models/team.model';

@Injectable({
  providedIn: 'root',
})
export class TeamService {
  constructor(private api: ApiService) {}

  getTeamMembers(teamId: number): Observable<User[]> {
    return this.api
      .get<User[]>(`team/${teamId}/users`)
      .pipe(tap((users) => console.log('Team members fetched:', users)));
  }

  getMe(): Observable<User> {
    return this.api.get<User>('user/me')
  }

  getAllUsers(): Observable<User[]> {
    return this.api.get<User[]>('user');
  }

  addMember(teamId: number, userEmail: string): Observable<User> {
    return this.api.post(`team/${teamId}/add-user`, { userEmail });
  }

  loadTeam(id: number): Observable<Team> {
    return this.api.get<Team>(`team/${id}`);
  }
}
