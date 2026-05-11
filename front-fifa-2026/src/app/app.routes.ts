
import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home';

import { Login } from './features/auth/pages/login/login';
import { Register } from './features/auth/pages/register/register';
import { AuthGuard } from './core/guards/auth.guard';
import { ForgotPasswordComponent } from './features/auth/pages/forgot-password/forgot-password.component';
import { Settings } from './features/settings/settings';
import { TicketsComponent } from './features/tickets/tickets.component';
import { TeamsComponent } from './features/teams/teams.component';
import { EstadisticasComponent } from './features/estadisticas/estadisticas.component';
import { ApuestasComponent } from './features/apuestas/apuestas.component';
import { CalendarioComponent } from './features/calendario/calendario.component';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  {
    path: 'album',
    canActivate: [AuthGuard],
    loadChildren: () =>
      import('./features/album/album-module').then(m => m.AlbumModule),
  },
  {
    path: 'home',
    canActivate: [AuthGuard],
    component: HomeComponent
  },
  {
    path: 'settings',
    canActivate: [AuthGuard],
    component: Settings
  },

  {
    path: 'equipos',
    canActivate: [AuthGuard],
    component: TeamsComponent
  },

  {
    path: 'tickets',
    canActivate: [AuthGuard],
    component: TicketsComponent
  },
  {
    path: 'calendario',
    canActivate: [AuthGuard],
    component: CalendarioComponent
  },

  { path: 'user', redirectTo: 'album', pathMatch: 'full' },
  {
    path: 'admin',
    canActivate: [AuthGuard],
    data: { expectedRole: 'ADMIN' },
    loadComponent: () => import('./features/admin/admin.component').then(m => m.AdminComponent)
  },

  {
    path: 'support',
    canActivate: [AuthGuard],
    data: { expectedRole: 'SUPPORT' }, 
    loadComponent: () => import('./features/support/support.component').then(m => m.SupportComponent)
  },

  {
    path: 'estadisticas',
    canActivate: [AuthGuard],
    component: EstadisticasComponent
  },

  {
    path: 'apuestas',
    canActivate: [AuthGuard],
    component: ApuestasComponent
  },

  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }, 
];