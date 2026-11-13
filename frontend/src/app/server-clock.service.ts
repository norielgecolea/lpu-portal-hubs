import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';

interface ServerTimeResponse {
  serverTime: string;
  zoneId: string;
  utcOffset: string;
}

/** Follows the Tomcat host clock and shows it in Asia/Manila. */
@Injectable({ providedIn: 'root' })
export class ServerClockService {
  private readonly http = inject(HttpClient);

  readonly now = signal<Date | null>(null);
  readonly datePipeTimezone = signal('+0800');

  private skewMs = 0;
  private started = false;
  private synced = false;

  start(): void {
    if (this.started) {
      return;
    }
    this.started = true;
    this.sync();
    window.setInterval(() => this.tick(), 1000);
    window.setInterval(() => this.sync(), 60_000);
    document.addEventListener('visibilitychange', () => {
      if (document.visibilityState === 'visible') {
        this.sync();
      }
    });
  }

  private tick(): void {
    if (!this.synced) {
      return;
    }
    this.now.set(new Date(Date.now() + this.skewMs));
  }

  private sync(): void {
    const sentAt = Date.now();
    this.http.get<ServerTimeResponse>('/hubs-portal/api/public/time').subscribe({
      next: (response) => {
        const receivedAt = Date.now();
        const serverEpoch = Date.parse(response.serverTime);
        if (Number.isNaN(serverEpoch)) {
          return;
        }
        this.skewMs = serverEpoch + Math.floor((receivedAt - sentAt) / 2) - receivedAt;
        this.datePipeTimezone.set(response.utcOffset.replace(':', '') || '+0800');
        this.synced = true;
        this.tick();
      },
      error: () => undefined,
    });
  }
}
