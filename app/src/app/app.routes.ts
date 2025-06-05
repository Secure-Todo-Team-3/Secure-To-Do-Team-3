import { Routes } from '@angular/router';
import { LoginComponent } from '@pages/login/login.component';
import { SignupComponent } from '@pages/signup/signup.component';
import { TeamsPageComponent } from '@pages/teams/teams-page.component';
import { TodoPageComponent } from '@pages/todo/todo.component';

export const routes: Routes = [
  // { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'signup', component: SignupComponent },
  { path: 'login', component: LoginComponent },
  {path: '', component: TodoPageComponent},
  {path: 'todo', component: TodoPageComponent},
  {path: 'teams', component: TeamsPageComponent},
  // { path: '**', redirectTo: '/login' }
];
