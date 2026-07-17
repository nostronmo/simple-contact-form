import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'home',
  },
  {
    path: 'home',
    loadComponent: () => import('./pages/home/home').then((m) => m.Home),
    // data: { hideLayout: false }
  },
  {
    path: 'error',
    loadChildren: () => import('./pages/errors/errors.routes').then((m) => m.errorRoutes),
  },
  {
    path: '**',
    redirectTo: 'error/404',
  },
];
