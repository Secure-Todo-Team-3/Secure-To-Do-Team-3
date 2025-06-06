import { Routes } from '@angular/router';
import { TaskEditPageComponent } from '@pages/edit-task.component/task-edit.component';
import { LoginComponent } from '@pages/login/login.component';
import { SignupComponent } from '@pages/signup/signup.component';
import { TeamEditComponent } from '@pages/team-edit/team-edit.component';
import { TeamMembersComponent } from '@pages/team-members.component';
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
  {path: 'edit-task', component: TaskEditPageComponent},
  {path: 'team-members', component: TeamMembersComponent},
  {path: 'edit-team', component: TeamEditComponent},
  {path: 'team-tasks', component: TeamTasksComponent},
  // { path: '**', redirectTo: '/login' }
];
