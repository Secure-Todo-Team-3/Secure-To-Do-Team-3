import { Routes } from '@angular/router';
import { TaskEditPageComponent } from '@pages/edit-task/task-edit.component';
import { LoginComponent } from '@pages/login/login.component';
import { SignupComponent } from '@pages/signup/signup.component';
import { TeamEditComponent } from '@pages/team-edit/team-edit.component';
import { TeamMembersComponent } from '@pages/team-members/team-members.component';
import { TeamTasksComponent } from '@pages/teams-tasks/team-tasks.component';
import { TeamsPageComponent } from '@pages/teams/teams-page.component';
import { TodoPageComponent } from '@pages/todo/todo.component';
import { publicGuard } from './core/guards/publicGuards';
import { authGuard } from './core/guards/authGuard';
import { ReportComponent } from '@pages/report/report.component';

export const routes: Routes = [
  { path: 'signup', component: SignupComponent,canActivate: [publicGuard] },
  { path: 'login', component: LoginComponent,canActivate: [publicGuard] },
  {path: '', component: ReportComponent,canActivate: [authGuard]},
  {path: 'tasks', component: TodoPageComponent,canActivate: [authGuard]},
  {path: 'teams', component: TeamsPageComponent,canActivate: [authGuard]},
  {path: 'edit-task/:id', component: TaskEditPageComponent,canActivate: [authGuard]},
  {path: 'create-task', component: TaskEditPageComponent, canActivate: [authGuard]},
  {path: 'team-members/:id', component: TeamMembersComponent,canActivate: [authGuard]},
  {path: 'edit-team/:id', component: TeamEditComponent,canActivate: [authGuard]},
  {path: 'create-team', component: TeamEditComponent,canActivate: [authGuard]},
  {path: 'team-tasks/:id', component: TeamTasksComponent,canActivate: [authGuard]},
  {path: 'reports', component: ReportComponent,canActivate: [authGuard] },
];
