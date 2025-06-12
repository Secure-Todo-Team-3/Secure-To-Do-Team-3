

import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ApiService } from 'src/app/shared/services/api.service';
import { Team } from 'src/app/shared/models/team.model';
import { User } from './shared/models/user.model';

@Injectable({ providedIn: 'root' })
export class AppService {
  constructor(private api: ApiService) {}

  getUser(): Observable<string> {
    return this.api.get<User>('user/me').pipe(
      map((user) => {
        return user.username;
      })
    );
  }
}