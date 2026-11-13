import { Component, OnInit, inject, signal } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { fallbackPortal } from './fallback-portals';
import { LinkIcon } from './link-icon';
import { HubLink, PortalView, documentTitle, resolvePortalCode } from './portal';
import { PortalService } from './portal.service';

type LocalNetworkRequestInit = RequestInit & {
  targetAddressSpace?: 'local' | 'loopback' | 'private' | 'public';
};

@Component({
  selector: 'app-hub-page',
  imports: [LinkIcon],
  templateUrl: './hub-page.html',
  styleUrl: './hub-page.css',
})
export class HubPage implements OnInit {
  private readonly portals = inject(PortalService);
  private readonly title = inject(Title);

  protected readonly portal = signal<PortalView>(fallbackPortal(resolvePortalCode()));
  /** null = still checking, true/false = result */
  protected readonly internetReachable = signal<boolean | null>(null);

  private probedUrl: string | null = null;

  ngOnInit(): void {
    const code = resolvePortalCode();
    this.portal.set(fallbackPortal(code));
    this.title.setTitle(documentTitle(this.portal().title));
    this.probeInternet();
    this.portals.load(code).subscribe({
      next: (portal) => {
        this.portal.set(portal);
        this.title.setTitle(documentTitle(portal.title));
        this.probeInternet();
      },
      error: () => {
        // The seeded catalog stays on screen when the API is down.
      },
    });
  }

  protected isDisabled(link: HubLink): boolean {
    return link.networkOnly && this.internetReachable() !== true;
  }

  protected onLinkClick(event: Event, link: HubLink): void {
    if (this.isDisabled(link)) {
      event.preventDefault();
      return;
    }
    if (link.id != null) {
      this.portals.recordClick(this.portal().code, link.id);
    }
  }

  private probeInternet(): void {
    const link = this.portal().links.find((item) => item.networkOnly);
    if (!link || this.probedUrl === link.url) {
      return;
    }
    this.probedUrl = link.url;
    this.internetReachable.set(null);
    void this.checkInternetReachability(link.url);
  }

  private async checkInternetReachability(url: string): Promise<void> {
    // HTTPS pages block plain HTTP probes unless the browser declares
    // Local Network Access. Non-Chromium browsers often cannot probe at all —
    // leave the link enabled there instead of stuck off.
    if (this.isHttpsPage() && !this.canUseLocalNetworkFetch()) {
      this.internetReachable.set(true);
      return;
    }
    this.internetReachable.set(await this.isReachable(url));
  }

  private isHttpsPage(): boolean {
    return globalThis.location?.protocol === 'https:';
  }

  private canUseLocalNetworkFetch(): boolean {
    const chrome = (globalThis as { chrome?: unknown }).chrome;
    return typeof chrome === 'object' && chrome !== null;
  }

  private async isReachable(url: string, timeoutMs = 4000): Promise<boolean> {
    const probe = `${url}${url.includes('?') ? '&' : '?'}_=${Date.now()}`;
    const init: LocalNetworkRequestInit = {
      mode: 'no-cors',
      cache: 'no-store',
      signal: AbortSignal.timeout(timeoutMs),
      targetAddressSpace: 'local',
    };
    try {
      await fetch(probe, init);
      return true;
    } catch {
      try {
        const { targetAddressSpace: _ignored, ...fallback } = init;
        await fetch(probe, fallback);
        return true;
      } catch {
        return false;
      }
    }
  }
}
