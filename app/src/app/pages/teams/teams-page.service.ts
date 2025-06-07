import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../shared/services/api.service';
import { Team } from 'src/app/shared/models/team.model';

@Injectable({ providedIn: 'root' })
export class TeamsPageService {
  constructor(private api: ApiService) {}

  getMyTeams(): Observable<Team[]> {
    return this.api.get<Team[]>('/teams/my');
  }

  getJoinedTeams(): Observable<Team[]> {
    return this.api.get<Team[]>('/teams/joined');
  }
}