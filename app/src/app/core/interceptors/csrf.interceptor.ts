import { HttpEvent, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

const getCookie = (name: string): string | null => {
  if (typeof document === 'undefined') {
    return null; 
  }
  const nameLenPlus = (name.length + 1);
  return document.cookie
    .split(';')
    .map(c => c.trim())
    .filter(cookie => cookie.substring(0, nameLenPlus) === `${name}=`)
    .map(cookie => decodeURIComponent(cookie.substring(nameLenPlus)))[0] || null;
};

export const csrfInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {

  if (req.method === 'GET' || req.method === 'HEAD') {
    return next(req);
  }

  const csrfToken = getCookie('XSRF-TOKEN');

  if (csrfToken) {
    const clonedRequest = req.clone({
      headers: req.headers.set('X-XSRF-TOKEN', csrfToken),
    });
    return next(clonedRequest);
  }

  return next(req);
};