import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, throwError, catchError } from 'rxjs';
import { StorageService } from './storage.service';
import { AuthenticatedResponse, RegisterRequest, TotpSetupResponse, TotpVerificationRequest } from '../models/AuthModel';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(
    private httpClient: HttpClient,
    private router: Router,
    private storageService: StorageService
  ) { }

  registerAndInitiateTotp(request: RegisterRequest): Observable<TotpSetupResponse> {
    return this.httpClient.post<TotpSetupResponse>(`${this.apiUrl}/registerAndInitiateTotp`, request);
  }

  verifyRegistration(request: TotpVerificationRequest): Observable<AuthenticatedResponse> {
    return this.httpClient.post<AuthenticatedResponse>(`${this.apiUrl}/register/totp/verify`, request).pipe(
      tap((res) => {
        if (res.token) {
          this.storageService.setToken(res.token);
        }
      }),
      catchError((err) => throwError(() => err)) 
    );
  }

  login(username: string, password: string): Observable<AuthenticatedResponse> {
    return this.httpClient.post<AuthenticatedResponse>(`${this.apiUrl}/login`, { username, password }).pipe(
      tap((res) => {
        if (res.token && !res.totpRequired) {
          this.storageService.setToken(res.token);
          this.router.navigate(['/dashboard']);
        }
      }),
      catchError((err) => throwError(() => err))
    );
  }

  verifyLogin(request: TotpVerificationRequest): Observable<AuthenticatedResponse> {
    return this.httpClient.post<AuthenticatedResponse>(`${this.apiUrl}/login/verify-totp`, request).pipe(
      tap((res) => {
        if (res.token) {
          this.storageService.setToken(res.token);
        }
      }),
      catchError((err) => throwError(() => err))
    );
  }

  logout(): void {
    this.storageService.clearToken();
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return !!this.storageService.getToken();
  }
}