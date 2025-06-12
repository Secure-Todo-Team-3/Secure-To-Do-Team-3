import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { UserStore } from '../models/user.store';

@Injectable({
    providedIn: 'root'
})
export class UserStoreService {
    private userStore = new BehaviorSubject<UserStore | null>(null);

    constructor() {
        const storedUser = localStorage.getItem('userStore');
        if (storedUser) {
            this.userStore.next(JSON.parse(storedUser));
        }
    }

    getUserStore(): Observable<UserStore | null> {
        return this.userStore.asObservable();
    }

    getCurrentUser(): UserStore | null {
        return this.userStore.getValue();
    }

    updateUserStore(user: UserStore): void {
        this.userStore.next(user);
        localStorage.setItem('userStore', JSON.stringify(user));
    }

    clearUserStore(): void {
        this.userStore.next(null);
        localStorage.removeItem('userStore');
    }

    hasSystemRole(role: string): boolean {
        const user = this.getCurrentUser();
        return user?.systemRole === role;
    }

    hasTeamRole(role: string): boolean {
        const user = this.getCurrentUser();
        return user?.teamRole?.roleName === role;
    }

    isInTeam(teamName: string): boolean {
        const user = this.getCurrentUser();
        return user?.teamRole?.teamName === teamName;
    }
} 