export type PortalCode = 'ADMIN' | 'LEARNER';
export type PortalLayout = 'admin' | 'learner';

export interface HubLink {
  id: number | null;
  name: string;
  url: string;
  description: string;
  icon: string;
  downloadName: string | null;
  networkOnly: boolean;
  sortOrder: number;
  enabled?: boolean;
  clickCount?: number;
}

export interface PortalView {
  code: PortalCode;
  title: string;
  subtitle: string;
  welcomeLabel: string;
  layout: PortalLayout;
  links: HubLink[];
}

export function resolvePortalCode(location: Location = globalThis.location): PortalCode {
  const host = (location?.hostname || '').toLowerCase();
  if (host === 'adminportal.lpulaguna.com' || host.startsWith('adminportal.')) {
    return 'ADMIN';
  }
  if (host === 'learnershub.lpulaguna.com' || host.startsWith('learnershub.')) {
    return 'LEARNER';
  }
  const path = location?.pathname || '/';
  if (path === '/admin' || path.startsWith('/admin/')) {
    return 'ADMIN';
  }
  if (path === '/learner' || path.startsWith('/learner/')) {
    return 'LEARNER';
  }
  const portal = new URLSearchParams(location?.search || '').get('portal');
  if (portal === 'admin') {
    return 'ADMIN';
  }
  if (portal === 'learner') {
    return 'LEARNER';
  }
  return 'LEARNER';
}

export function documentTitle(title: string): string {
  return `LPU ${title}`;
}
