import { TestBed } from '@angular/core/testing';
import { HttpRequest, HttpHandlerFn, HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from '../auth/auth.service';
import { User } from '../../models';

const mockUser: User = { id: 42, name: 'Mario', email: 'mario@test.com', projectIds: [] };

describe('authInterceptor', () => {
  let auth: AuthService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
    auth = TestBed.inject(AuthService);
  });

  afterEach(() => localStorage.clear());

  const runInterceptor = (captured: { req?: HttpRequest<unknown> }) => {
    const next: HttpHandlerFn = (req) => {
      captured.req = req;
      return of(new HttpResponse({ status: 200 }));
    };
    const req = new HttpRequest('GET', '/api/test');
    TestBed.runInInjectionContext(() => authInterceptor(req, next)).subscribe();
  };

  it('adds X-User-Id header when user is authenticated', () => {
    auth.login(mockUser);
    const captured: { req?: HttpRequest<unknown> } = {};
    runInterceptor(captured);
    expect(captured.req!.headers.get('X-User-Id')).toBe('42');
  });

  it('does not add X-User-Id header when no user is authenticated', () => {
    const captured: { req?: HttpRequest<unknown> } = {};
    runInterceptor(captured);
    expect(captured.req!.headers.has('X-User-Id')).toBe(false);
  });
});
