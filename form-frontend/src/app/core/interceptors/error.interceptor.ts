import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpInterceptorFn,
  HttpRequest,
} from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  private router = inject(Router);

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        const isAuthRoute = request.url.includes('/auth/');
        if (
          error.status === 0 ||
          error.status === 502 ||
          error.status === 503 ||
          error.status === 504
        ) {
          this.router.navigate(['/error/500']);
        }

        if (error.status === 429) {
          this.router.navigate(['/error/429']);
        }

        if ((error.status === 403 || error.status === 401) && !isAuthRoute) {
          this.router.navigate(['/error/401']);
        }

        return throwError(() => error);
      }),
    );
  }
}
