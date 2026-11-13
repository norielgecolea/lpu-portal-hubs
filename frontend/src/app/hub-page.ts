import { DatePipe } from '@angular/common';
import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild, inject, signal } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { fallbackPortal } from './fallback-portals';
import { LinkIcon } from './link-icon';
import { HubLink, PortalView, documentTitle, resolvePortalCode } from './portal';
import { PortalService } from './portal.service';
import { ServerClockService } from './server-clock.service';

type LocalNetworkRequestInit = RequestInit & {
  targetAddressSpace?: 'local' | 'loopback' | 'private' | 'public';
};

@Component({
  selector: 'app-hub-page',
  imports: [LinkIcon, DatePipe],
  templateUrl: './hub-page.html',
  styleUrl: './hub-page.css',
})
export class HubPage implements OnInit, AfterViewInit, OnDestroy {
  private readonly portals = inject(PortalService);
  private readonly title = inject(Title);
  protected readonly clock = inject(ServerClockService);

  @ViewChild('mark') private mark?: ElementRef<HTMLImageElement>;
  @ViewChild('slot') private slot?: ElementRef<HTMLElement>;

  protected readonly reduceMotion = globalThis.matchMedia?.('(prefers-reduced-motion: reduce)').matches ?? false;
  protected readonly opened = signal(this.reduceMotion);
  protected readonly leaving = signal(this.reduceMotion);
  protected readonly portal = signal<PortalView>(fallbackPortal(resolvePortalCode()));
  /** null = still checking, true/false = result */
  protected readonly internetReachable = signal<boolean | null>(null);

  private probedUrl: string | null = null;
  private introTimer = 0;
  private settleTimer = 0;

  ngAfterViewInit(): void {
    if (this.reduceMotion || !this.mark || !this.slot) {
      return;
    }
    this.introTimer = window.setTimeout(() => this.parkLogo(), 780);
  }

  ngOnDestroy(): void {
    window.clearTimeout(this.introTimer);
    window.clearTimeout(this.settleTimer);
  }

  ngOnInit(): void {
    const code = resolvePortalCode();
    this.portal.set(fallbackPortal(code));
    this.title.setTitle(documentTitle(this.portal().title));
    this.clock.start();
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

  protected note(link: HubLink): string {
    if (link.networkOnly) {
      return this.internetReachable() === null ? 'Checking the campus network…' : 'Campus network only';
    }
    if (link.icon === 'LIT') {
      return 'Android app';
    }
    return link.description;
  }

  private parkLogo(): void {
    const mark = this.mark?.nativeElement;
    const slot = this.slot?.nativeElement;
    if (!mark || !slot) {
      this.opened.set(true);
      return;
    }
    const from = mark.getBoundingClientRect();
    const to = slot.getBoundingClientRect();
    if (from.height < 8 || to.height < 8) {
      this.opened.set(true);
      return;
    }

    mark.style.animation = 'none';
    mark.style.position = 'fixed';
    mark.style.margin = '0';
    mark.style.left = `${from.left}px`;
    mark.style.top = `${from.top}px`;
    mark.style.width = `${from.width}px`;
    mark.style.height = `${from.height}px`;
    mark.style.maxHeight = 'none';
    mark.style.transform = 'none';

    this.leaving.set(true);
    requestAnimationFrame(() => {
      mark.style.transition = 'left 0.72s cubic-bezier(0.2, 0.7, 0.2, 1), top 0.72s cubic-bezier(0.2, 0.7, 0.2, 1), width 0.72s cubic-bezier(0.2, 0.7, 0.2, 1), height 0.72s cubic-bezier(0.2, 0.7, 0.2, 1)';
      mark.style.left = `${to.left}px`;
      mark.style.top = `${to.top}px`;
      mark.style.width = `${to.width}px`;
      mark.style.height = `${to.height}px`;
    });

    const finish = (event?: Event) => {
      if (event instanceof TransitionEvent && event.propertyName !== 'left') {
        return;
      }
      mark.removeEventListener('transitionend', finish);
      this.opened.set(true);
    };
    mark.addEventListener('transitionend', finish);
    this.settleTimer = window.setTimeout(() => finish(), 900);
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
