import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { provideAnimations } from '@angular/platform-browser/animations';
import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';
import { provideHttpClient, withInterceptors, withInterceptorsFromDi } from '@angular/common/http';
import { authInterceptor } from './app/core/interceptors/authInterceptor';
import { credentialsInterceptor } from './app/core/interceptors/credentials.interceptor';
import { csrfInterceptor } from './app/core/interceptors/csrf.interceptor';
bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(withInterceptors([authInterceptor,credentialsInterceptor,csrfInterceptor])),
    provideRouter(routes),
    provideAnimations(),
  ]
}).catch(err => console.error(err));