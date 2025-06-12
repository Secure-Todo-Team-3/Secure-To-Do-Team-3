import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { StorageService } from '../services/storage.service';
import { environment } from 'src/app/shared/environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(StorageService);
  const token = authService.getToken();
  
  const apiUrl = environment.apiUrl+'/';

  if (token && req.url.startsWith(apiUrl)) {
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    
    return next(clonedRequest);
  }

  return next(req);
};
