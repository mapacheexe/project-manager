import { inject } from '@angular/core';
import { HttpInterceptorFn } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const userId = inject(AuthService).currentUserId();

  if (userId === null) return next(req);

  return next(req.clone({ setHeaders: { 'X-User-Id': String(userId) } }));
};
