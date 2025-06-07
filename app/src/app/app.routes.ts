import { Routes } from '@angular/router';
import { TaskEditPageComponent } from '@pages/edit-task/task-edit.component';
import { LoginComponent } from '@pages/login/login.component';
import { SignupComponent } from '@pages/signup/signup.component';
import { TeamEditComponent } from '@pages/team-edit/team-edit.component';
import { TeamMembersComponent } from '@pages/team-members/team-members.component';
import { TeamTasksComponent } from '@pages/teams-tasks/team-tasks.component';
import { TeamsPageComponent } from '@pages/teams/teams-page.component';
import { TodoPageComponent } from '@pages/todo/todo.component';

export const routes: Routes = [
  // { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'signup', component: SignupComponent },
  { path: 'login', component: LoginComponent },
  {path: '', component: TodoPageComponent},
  {path: 'tasks', component: TodoPageComponent},
  {path: 'teams', component: TeamsPageComponent},
  {path: 'edit-task/:id', component: TaskEditPageComponent},
  {path: 'create-task', component: TaskEditPageComponent},
  {path: 'team-members/:id', component: TeamMembersComponent},
  {path: 'edit-team/:id', component: TeamEditComponent},
  {path: 'create-team', component: TeamEditComponent},
  {path: 'team-tasks/:id', component: TeamTasksComponent},
  // { path: '**', redirectTo: '/login' }
];
