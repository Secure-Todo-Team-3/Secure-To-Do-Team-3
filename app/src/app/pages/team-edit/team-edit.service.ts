import { Injectable } from '@angular/core';
import { ApiService } from '../../shared/services/api.service';
import { Observable } from 'rxjs';
import { Team } from 'src/app/shared/models/team.model';

@Injectable({ providedIn: 'root' })
export class TeamEditService {
  constructor(private api: ApiService) {}

  saveTeam(team: Team, isEditMode: boolean): Observable<void> {
    return isEditMode
      ? this.api.put<void>(`teams/${team.id}`, team)
      : this.api.post<void>(`teams`, team);
  }
}
