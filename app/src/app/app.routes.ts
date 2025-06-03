import { Routes } from '@angular/router';
import { LoginComponent } from '@pages/login/login.component';
import { SignupComponent } from '@pages/signup/signup.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'signup', component: SignupComponent },
  { path: 'login', component: LoginComponent },
  { path: '**', redirectTo: '/login' }
];
