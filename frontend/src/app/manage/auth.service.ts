import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';

export interface Session {
  token: string;
  username: string;
  role: string;
  passwordChangeRequired: boolean;
}

interface LoginResponse {
  token: string;
  username: string;
  role: string;
  passwordChangeRequired: boolean;
}

const STORAGE_KEY = 'lpu-hubs-token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly base = '/hubs-portal/api/auth';

  login(username: string, password: string, rememberMe: boolean): Observable<Session> {
    return this.http
      .post<LoginResponse>(`${this.base}/login`, { username, password, rememberMe })
      .pipe(tap((session) => this.persist(session.token, rememberMe)));
  }

  changePassword(currentPassword: string, newPassword: string): Observable<Session> {
    return this.http
      .post<LoginResponse>(`${this.base}/change-password`, { currentPassword, newPassword }, { headers: this.headers() })
      .pipe(tap((session) => this.persist(session.token, this.remembered())));
  }

  me(): Observable<{ username: string; role: string; passwordChangeRequired: boolean }> {
    return this.http.get<{ username: string; role: string; passwordChangeRequired: boolean }>(`${this.base}/me`, {
      headers: this.headers(),
    });
  }

  logout(): void {
    const token = this.token();
    this.clear();
    if (!token) {
      return;
    }
    void fetch(`${this.base}/logout`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
      keepalive: true,
    });
  }

  token(): string | null {
    return sessionStorage.getItem(STORAGE_KEY) || localStorage.getItem(STORAGE_KEY);
  }

  clear(): void {
    sessionStorage.removeItem(STORAGE_KEY);
    localStorage.removeItem(STORAGE_KEY);
  }

  static message(error: unknown, fallback: string): string {
    if (error instanceof HttpErrorResponse) {
      const body = error.error as { message?: string } | null;
      if (body && typeof body.message === 'string' && body.message.trim()) {
        return body.message;
      }
      if (error.status === 0) {
        return 'The portal service is not reachable.';
      }
    }
    return fallback;
  }

  private headers(): { Authorization: string } {
    return { Authorization: `Bearer ${this.token() ?? ''}` };
  }

  private persist(token: string, rememberMe: boolean): void {
    this.clear();
    if (rememberMe) {
      localStorage.setItem(STORAGE_KEY, token);
    } else {
      sessionStorage.setItem(STORAGE_KEY, token);
    }
  }

  private remembered(): boolean {
    return localStorage.getItem(STORAGE_KEY) != null;
  }
}
