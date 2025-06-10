import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ApiService } from '../../shared/services/api.service';
import { Team } from 'src/app/shared/models/team.model';

@Injectable({ providedIn: 'root' })
export class TeamsPageService {
  constructor(private api: ApiService) {}

  getMyTeams(): Observable<Team[]> {
    return this.api.get<Team[]>('team/user-teams', {type: 'team_lead'}).pipe(
      map(teams => teams.map( team => ({ ...team, isLead: true })))
    );
  }

  getJoinedTeams(): Observable<Team[]> {
    return this.api.get<Team[]>('team/user-teams', {type: 'member'}).pipe(
      map(teams => teams.map( team => ({ ...team, isLead: false })))
    );
  }
}