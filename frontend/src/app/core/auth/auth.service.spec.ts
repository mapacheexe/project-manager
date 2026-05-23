import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { User } from '../../models';

const mockUser: User = { id: 1, name: 'Mario', email: 'mario@test.com', projectIds: [] };
const mockToken = 'token.jwt.here';

describe('AuthService', () => {
  let service: AuthService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthService);
  });

  afterEach(() => localStorage.clear());

  describe('login', () => {
    it('sets currentUser signal', () => {
      service.login(mockUser, mockToken);
      expect(service.currentUser()).toEqual(mockUser);
    });

    it('sets token signal', () => {
      service.login(mockUser, mockToken);
      expect(service.token()).toBe(mockToken);
    });

    it('persists user in localStorage', () => {
      service.login(mockUser, mockToken);
      expect(JSON.parse(localStorage.getItem('currentUser')!)).toEqual(mockUser);
    });

    it('persists token in localStorage', () => {
      service.login(mockUser, mockToken);
      expect(localStorage.getItem('authToken')).toBe(mockToken);
    });

    it('exposes currentUserId', () => {
      service.login(mockUser, mockToken);
      expect(service.currentUserId()).toBe(1);
    });
  });

  describe('logout', () => {
    it('clears currentUser signal', () => {
      service.login(mockUser, mockToken);
      service.logout();
      expect(service.currentUser()).toBeNull();
    });

    it('clears token signal', () => {
      service.login(mockUser, mockToken);
      service.logout();
      expect(service.token()).toBeNull();
    });

    it('removes user from localStorage', () => {
      service.login(mockUser, mockToken);
      service.logout();
      expect(localStorage.getItem('currentUser')).toBeNull();
    });

    it('removes token from localStorage', () => {
      service.login(mockUser, mockToken);
      service.logout();
      expect(localStorage.getItem('authToken')).toBeNull();
    });
  });

  describe('updateCurrentUser', () => {
    it('updates signal and localStorage', () => {
      service.login(mockUser, mockToken);
      const updated: User = { ...mockUser, name: 'Luigi' };
      service.updateCurrentUser(updated);
      expect(service.currentUser()).toEqual(updated);
      expect(JSON.parse(localStorage.getItem('currentUser')!)).toEqual(updated);
    });
  });

  describe('initial load', () => {
    it('restores user and token from localStorage on init', () => {
      localStorage.setItem('currentUser', JSON.stringify(mockUser));
      localStorage.setItem('authToken', mockToken);
      TestBed.resetTestingModule();
      TestBed.configureTestingModule({});
      const fresh = TestBed.inject(AuthService);
      expect(fresh.currentUser()).toEqual(mockUser);
      expect(fresh.token()).toBe(mockToken);
    });
  });
});
