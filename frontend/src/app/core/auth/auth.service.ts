import { computed, Injectable, signal } from '@angular/core';
import { User } from '../../models';

const USER_KEY = 'currentUser';
const TOKEN_KEY = 'authToken';

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly currentUser = signal<User | null>(this.loadUserFromStorage());
  readonly currentUserId = computed(() => this.currentUser()?.id ?? null);
  readonly token = signal<string | null>(localStorage.getItem(TOKEN_KEY));

  login(user: User, token: string): void {
    this.currentUser.set(user);
    this.token.set(token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    localStorage.setItem(TOKEN_KEY, token);
  }

  updateCurrentUser(user: User): void {
    this.currentUser.set(user);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  logout(): void {
    this.currentUser.set(null);
    this.token.set(null);
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(TOKEN_KEY);
  }

  private loadUserFromStorage(): User | null {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? (JSON.parse(raw) as User) : null;
  }
}
