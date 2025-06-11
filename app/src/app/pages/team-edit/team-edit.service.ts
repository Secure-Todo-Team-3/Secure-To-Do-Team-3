import { Injectable } from '@angular/core';
import { ApiService } from '../../shared/services/api.service';
import { Observable } from 'rxjs';
import { Team } from 'src/app/shared/models/team.model';

@Injectable({ providedIn: 'root' })
export class TeamEditService {
  constructor(private api: ApiService) {}

  saveTeam(team: Team, isEditMode: boolean, oldTeam?: Team): Observable<void> {
    if (isEditMode) {
      return this.api.post<void>(`team/${team.id}/update`, {
        ...(oldTeam?.name !== team.name && { name: team.name }),
        ...(oldTeam?.description !== team.description && { description: team.description }),
      });
    } else {
      return this.api.post<void>('team/create', team);
    }
  }

  loadTeam(id: number): Observable<Team> {
    return this.api.get<Team>(`team/${id}`);
  }
}
