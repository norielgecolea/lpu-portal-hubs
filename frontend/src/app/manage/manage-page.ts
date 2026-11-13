import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';
import { LINK_ICONS } from '../link-icons';
import { PortalCode } from '../portal';
import { AdminLink, LinkWrite, PortalService } from '../portal.service';
import { AuthService, Session } from './auth.service';

interface DraftLink {
  id: number | null;
  name: string;
  url: string;
  description: string;
  icon: string;
  downloadName: string;
  networkOnly: boolean;
  sortOrder: number;
  enabled: boolean;
  clickCount: number;
  saving: boolean;
}

interface DraftPortal {
  code: PortalCode;
  title: string;
  subtitle: string;
  welcomeLabel: string;
  links: DraftLink[];
  saving: boolean;
  draft: DraftLink;
}

@Component({
  selector: 'app-manage-page',
  imports: [FormsModule, RouterLink],
  templateUrl: './manage-page.html',
})
export class ManagePage implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly portals = inject(PortalService);
  private readonly title = inject(Title);

  protected readonly icons = LINK_ICONS;
  protected readonly session = signal<Session | null>(null);
  protected readonly loading = signal(false);
  protected readonly error = signal('');
  protected readonly notice = signal('');
  protected readonly portalsDraft = signal<DraftPortal[]>([]);

  protected username = '';
  protected password = '';
  protected rememberMe = false;
  protected currentPassword = '';
  protected newPassword = '';

  ngOnInit(): void {
    this.title.setTitle('LPU Portal Links');
    const token = this.auth.token();
    if (!token) {
      return;
    }
    this.loading.set(true);
    this.auth.me().subscribe({
      next: (me) => {
        this.session.set({
          token,
          username: me.username,
          role: me.role,
          passwordChangeRequired: me.passwordChangeRequired,
        });
        this.loading.set(false);
        if (!me.passwordChangeRequired) {
          this.loadPortals();
        }
      },
      error: () => {
        this.auth.clear();
        this.loading.set(false);
      },
    });
  }

  protected signIn(): void {
    this.error.set('');
    this.loading.set(true);
    this.auth.login(this.username.trim(), this.password, this.rememberMe).subscribe({
      next: (session) => {
        this.session.set(session);
        this.password = '';
        this.loading.set(false);
        if (!session.passwordChangeRequired) {
          this.loadPortals();
        }
      },
      error: (error: unknown) => {
        this.loading.set(false);
        this.error.set(AuthService.message(error, 'Sign-in failed.'));
      },
    });
  }

  protected changePassword(): void {
    this.error.set('');
    this.loading.set(true);
    this.auth.changePassword(this.currentPassword, this.newPassword).subscribe({
      next: (session) => {
        this.session.set(session);
        this.currentPassword = '';
        this.newPassword = '';
        this.loading.set(false);
        this.notice.set('Password updated.');
        this.loadPortals();
      },
      error: (error: unknown) => {
        this.loading.set(false);
        this.error.set(AuthService.message(error, 'Could not change the password.'));
      },
    });
  }

  protected signOut(): void {
    this.auth.logout();
    this.session.set(null);
    this.portalsDraft.set([]);
    this.notice.set('');
  }

  protected saveHeading(portal: DraftPortal): void {
    const token = this.session()?.token;
    if (!token) {
      return;
    }
    portal.saving = true;
    this.error.set('');
    this.portals.updatePortal(token, portal.code, {
      title: portal.title.trim(),
      subtitle: portal.subtitle.trim(),
      welcomeLabel: portal.welcomeLabel.trim(),
    }).subscribe({
      next: () => {
        portal.saving = false;
        this.notice.set(`${portal.title} heading saved.`);
      },
      error: (error: unknown) => {
        portal.saving = false;
        this.error.set(AuthService.message(error, 'Could not save the heading.'));
      },
    });
  }

  protected saveLink(portal: DraftPortal, link: DraftLink): void {
    const token = this.session()?.token;
    if (!token) {
      return;
    }
    link.saving = true;
    this.error.set('');
    const body = this.toWrite(link);
    const request = link.id == null
      ? this.portals.createLink(token, portal.code, body)
      : this.portals.updateLink(token, link.id, body);
    request.subscribe({
      next: (saved) => {
        if (link.id == null) {
          portal.links = [...portal.links, this.fromLink(saved)].sort((a, b) => a.sortOrder - b.sortOrder);
          portal.draft = this.blankDraft(portal);
        } else {
          Object.assign(link, this.fromLink(saved));
        }
        link.saving = false;
        this.notice.set(`${saved.name} saved.`);
      },
      error: (error: unknown) => {
        link.saving = false;
        this.error.set(AuthService.message(error, 'Could not save the link.'));
      },
    });
  }

  protected removeLink(portal: DraftPortal, link: DraftLink): void {
    if (link.id == null) {
      return;
    }
    const token = this.session()?.token;
    if (!token || !globalThis.confirm(`Remove ${link.name}?`)) {
      return;
    }
    this.portals.deleteLink(token, link.id).subscribe({
      next: () => {
        portal.links = portal.links.filter((item) => item.id !== link.id);
        this.notice.set(`${link.name} removed.`);
      },
      error: (error: unknown) => {
        this.error.set(AuthService.message(error, 'Could not remove the link.'));
      },
    });
  }

  private loadPortals(): void {
    const token = this.session()?.token;
    if (!token) {
      return;
    }
    this.loading.set(true);
    this.portals.adminPortals(token).subscribe({
      next: (portals) => {
        this.portalsDraft.set(portals.map((portal) => ({
          code: portal.code,
          title: portal.title,
          subtitle: portal.subtitle,
          welcomeLabel: portal.welcomeLabel,
          saving: false,
          links: portal.links.map((link) => this.fromLink(link)),
          draft: this.blankDraft({ links: portal.links.map((link) => this.fromLink(link)) }),
        })));
        this.loading.set(false);
      },
      error: (error: unknown) => {
        this.loading.set(false);
        this.error.set(AuthService.message(error, 'Could not load the portals.'));
      },
    });
  }

  private fromLink(link: AdminLink): DraftLink {
    return {
      id: link.id,
      name: link.name,
      url: link.url,
      description: link.description,
      icon: link.icon,
      downloadName: link.downloadName ?? '',
      networkOnly: link.networkOnly,
      sortOrder: link.sortOrder,
      enabled: link.enabled,
      clickCount: link.clickCount,
      saving: false,
    };
  }

  private blankDraft(portal: { links: DraftLink[] }): DraftLink {
    const next = portal.links.reduce((max, link) => Math.max(max, link.sortOrder), 0) + 10;
    return {
      id: null,
      name: '',
      url: 'https://',
      description: '',
      icon: 'LMS',
      downloadName: '',
      networkOnly: false,
      sortOrder: next,
      enabled: true,
      clickCount: 0,
      saving: false,
    };
  }

  private toWrite(link: DraftLink): LinkWrite {
    const downloadName = link.downloadName.trim();
    return {
      name: link.name.trim(),
      url: link.url.trim(),
      description: link.description.trim(),
      icon: link.icon.trim(),
      sortOrder: Number(link.sortOrder),
      downloadName: downloadName ? downloadName : null,
      networkOnly: link.networkOnly,
      enabled: link.enabled,
    };
  }
}
