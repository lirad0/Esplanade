import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () => import('./pages/home/home.page').then((m) => m.HomePage)
  },
  {
    path: 'edit',
    loadComponent: () => import('./pages/edit/edit.page').then((m) => m.EditPage)
  }
];
