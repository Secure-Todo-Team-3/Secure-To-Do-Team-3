import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, throwError, catchError, concatMap, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { StorageService } from './storage.service';
import { AuthenticatedResponse, RegisterRequest, TotpSetupResponse, TotpVerificationRequest } from '../models/AuthModel';
import { environment } from 'src/app/shared/environments/environment';
import { UserStoreService } from './userStore.service';
import { UserStore } from '../models/user.store';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl + '/auth';
  private userApiUrl = environment.apiUrl + '/user';

  constructor(
    private httpClient: HttpClient,
    private router: Router,
    private storageService: StorageService,
    private userStoreService: UserStoreService
  ) { }

  registerAndInitiateTotp(request: RegisterRequest): Observable<TotpSetupResponse> {
    return this.httpClient.post<TotpSetupResponse>(`${this.apiUrl}/registerAndInitiateTotp`, request);
  }

  verifyRegistration(request: TotpVerificationRequest): Observable<AuthenticatedResponse> {
    const authResponse$ = this.httpClient.post<AuthenticatedResponse>(`${this.apiUrl}/register/totp/verify`, request);
    return this._primeCsrfToken(authResponse$);
  }

  login(username: string, password: string): Observable<AuthenticatedResponse> {
    const authResponse$ = this.httpClient.post<AuthenticatedResponse>(`${this.apiUrl}/login`, { username, password });

    return authResponse$.pipe(
      concatMap(res => {
        if (res.totpRequired) {
          return of(res);
        }
        return this._primeCsrfToken(of(res));
      }),
      catchError((err) => throwError(() => err))
    );
  }

  verifyLogin(request: TotpVerificationRequest): Observable<AuthenticatedResponse> {
    const authResponse$ = this.httpClient.post<AuthenticatedResponse>(`${this.apiUrl}/login/verify-totp`, request);
    return this._primeCsrfToken(authResponse$);
  }

  logout(): void {
    this.storageService.clearToken();
    this.userStoreService.clearUserStore();
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return !!this.storageService.getToken();
  }

  private _primeCsrfToken(authResponse$: Observable<AuthenticatedResponse>): Observable<AuthenticatedResponse> {
    return authResponse$.pipe(
      tap(res => {
        if (res.token) {
          this.storageService.setToken(res.token);
        }
      }),
      concatMap(res => {
        if (!res.token) {
          return of(res);
        }
        return this.httpClient.get<UserStore>(`${this.userApiUrl}/me`).pipe(
          tap(user => this.userStoreService.updateUserStore(user)),
          map(() => res)
        );
      })
    );
  }
}