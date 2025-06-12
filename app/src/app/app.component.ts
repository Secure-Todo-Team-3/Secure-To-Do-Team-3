import { Component, signal, ViewChild } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { MatSidenav, MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { NgIf } from '@angular/common';
import { getInitials } from './shared/utils/get-initials';
import { AuthService } from './core/services/auth.service';
import { AppService } from './app.service';

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  imports: [
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatButtonModule,
    MatIconModule,
    RouterModule,
    NgIf,
  ],
})
export class AppComponent {
  showSidenav = signal(false);
  username = signal('');
  appTitle = 'Task Manager Pro';
  getInitials = getInitials;
  isLoggedIn = false;
  @ViewChild('drawer') drawer!: MatSidenav;

  constructor(
    private router: Router,
    private authService: AuthService,
    private appService: AppService
  ) {
    this.isLoggedIn = this.authService.isLoggedIn();

    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => {
        const url = this.router.url;
        const shouldShowSidenav = !(
          url.includes('/login') || url.includes('/signup')
        );
        this.showSidenav.set(shouldShowSidenav);

        if (shouldShowSidenav) {
          this.appService.getUser().subscribe((name) => {
            if (name) {
              this.username.set(name);
            } else {
              this.username.set('Task Manager Pro');
            }
          });
        }
      });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
    this.showSidenav.set(false);
    this.drawer.close();
  }
}
