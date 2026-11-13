import { Routes } from '@angular/router';
import { HubPage } from './hub-page';

export const routes: Routes = [
  {
    path: 'manage',
    loadComponent: () => import('./manage/manage-page').then((m) => m.ManagePage),
  },
  { path: 'admin', component: HubPage },
  { path: 'learner', component: HubPage },
  { path: '', component: HubPage },
  { path: '**', component: HubPage },
];
