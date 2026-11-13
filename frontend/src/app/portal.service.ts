import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { PortalCode, PortalView } from './portal';

export interface AdminPortal extends PortalView {
  links: AdminLink[];
}

export interface AdminLink {
  id: number;
  name: string;
  url: string;
  description: string;
  icon: string;
  downloadName: string | null;
  networkOnly: boolean;
  sortOrder: number;
  enabled: boolean;
  clickCount: number;
}

export interface LinkWrite {
  name: string;
  url: string;
  description: string;
  icon: string;
  sortOrder: number;
  downloadName: string | null;
  networkOnly: boolean;
  enabled: boolean;
}

export interface PortalWrite {
  title: string;
  subtitle: string;
  welcomeLabel: string;
}

@Injectable({ providedIn: 'root' })
export class PortalService {
  private readonly http = inject(HttpClient);
  private readonly base = '/hubs-portal/api';

  load(code: PortalCode): Observable<PortalView> {
    return this.http.get<PortalView>(`${this.base}/public/portals/${code}`);
  }

  recordClick(code: PortalCode, id: number): void {
    void fetch(`${this.base}/public/portals/${code}/links/${id}/click`, {
      method: 'POST',
      keepalive: true,
    });
  }

  adminPortals(token: string): Observable<AdminPortal[]> {
    return this.http.get<AdminPortal[]>(`${this.base}/portals`, { headers: this.auth(token) });
  }

  updatePortal(token: string, code: PortalCode, body: PortalWrite): Observable<AdminPortal> {
    return this.http.put<AdminPortal>(`${this.base}/portals/${code}`, body, { headers: this.auth(token) });
  }

  createLink(token: string, code: PortalCode, body: LinkWrite): Observable<AdminLink> {
    return this.http.post<AdminLink>(`${this.base}/portals/${code}/links`, body, { headers: this.auth(token) });
  }

  updateLink(token: string, id: number, body: LinkWrite): Observable<AdminLink> {
    return this.http.put<AdminLink>(`${this.base}/links/${id}`, body, { headers: this.auth(token) });
  }

  deleteLink(token: string, id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/links/${id}`, { headers: this.auth(token) });
  }

  private auth(token: string): { Authorization: string } {
    return { Authorization: `Bearer ${token}` };
  }
}
