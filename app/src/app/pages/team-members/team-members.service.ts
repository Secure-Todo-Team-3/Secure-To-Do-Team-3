import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../shared/services/api.service';
import { User } from 'src/app/shared/models/user.model';
import { Role } from 'src/app/shared/models/role.model';

@Injectable({
  providedIn: 'root'
})
export class TeamMembersService {
  private readonly endpoint = '/team-members';

  constructor(private api: ApiService) {}

  getRoles(): Observable<Role[]> {
    return this.api.get<Role[]>('/roles');
  }

  getTeamMembers(teamId: string): Observable<User[]> {
    return this.api.get<User[]>(`${this.endpoint}/team/${teamId}`);
  }

  addMember(member: Partial<User>): Observable<User> {
    return this.api.post<User>(this.endpoint, member);
  }

  removeMember(teamId: string, memberId: string): Observable<void> {
    return this.api.delete<void>(`${this.endpoint}?teamId=${teamId}&memberId=${memberId}`);
  }

  updateMember(member: User): Observable<User> {
    return this.api.put<User>(`${this.endpoint}/${member.id}`, member);
  }

  searchUsers(query: string): Observable<User[]> {
    return this.api.get<User[]>(`/users`, { params: { q: query } });
  }
}