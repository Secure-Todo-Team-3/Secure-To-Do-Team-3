import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  private cacheToken : string |null = null;
  private TOKEN_KEY = 'token';

  setToken(token: string) {
    this.cacheToken = token;
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    if (this.cacheToken) {
      return this.cacheToken;
    }
    return localStorage.getItem(this.TOKEN_KEY);
  }

  clearToken() {
    this.cacheToken = null;
    localStorage.removeItem(this.TOKEN_KEY);
  }
}
