import { Component, signal } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { NgIf } from '@angular/common';

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
  appTitle = 'Task Manager Pro';
  userName = 'Alex Johnson';
  userEmail = 'alex.johnson@example.com';

  constructor(private router: Router) {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe(() => {
        this.showSidenav.set(
          !this.router.url.includes('/login') &&
            !this.router.url.includes('/signup')
        );
      });
  }

  logout() {
    console.log('Logging out...');
    this.router.navigate(['/login']);
    this.showSidenav.set(false);
  }
}
