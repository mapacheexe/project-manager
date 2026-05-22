import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { User } from '../../models';

const mockUser: User = { id: 1, name: 'Mario', email: 'mario@test.com', projectIds: [] };

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
      service.login(mockUser);
      expect(service.currentUser()).toEqual(mockUser);
    });

    it('persists user in localStorage', () => {
      service.login(mockUser);
      expect(JSON.parse(localStorage.getItem('currentUser')!)).toEqual(mockUser);
    });

    it('exposes currentUserId', () => {
      service.login(mockUser);
      expect(service.currentUserId()).toBe(1);
    });
  });

  describe('logout', () => {
    it('clears currentUser signal', () => {
      service.login(mockUser);
      service.logout();
      expect(service.currentUser()).toBeNull();
    });

    it('removes user from localStorage', () => {
      service.login(mockUser);
      service.logout();
      expect(localStorage.getItem('currentUser')).toBeNull();
    });
  });

  describe('updateCurrentUser', () => {
    it('updates signal and localStorage', () => {
      service.login(mockUser);
      const updated: User = { ...mockUser, name: 'Luigi' };
      service.updateCurrentUser(updated);
      expect(service.currentUser()).toEqual(updated);
      expect(JSON.parse(localStorage.getItem('currentUser')!)).toEqual(updated);
    });
  });

  describe('initial load', () => {
    it('restores user from localStorage on init', () => {
      localStorage.setItem('currentUser', JSON.stringify(mockUser));
      TestBed.resetTestingModule();
      TestBed.configureTestingModule({});
      const fresh = TestBed.inject(AuthService);
      expect(fresh.currentUser()).toEqual(mockUser);
    });
  });
});
