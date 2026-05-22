import { TestBed } from '@angular/core/testing';
import { ToastService } from './toast.service';

describe('ToastService', () => {
  let service: ToastService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ToastService);
  });

  describe('success', () => {
    it('adds a toast of type success', () => {
      service.success('Saved');
      const toasts = service.toasts();
      expect(toasts.length).toBe(1);
      expect(toasts[0]).toEqual(expect.objectContaining({ message: 'Saved', type: 'success' }));
    });
  });

  describe('error', () => {
    it('adds a toast of type error', () => {
      service.error('Something went wrong');
      const toasts = service.toasts();
      expect(toasts.length).toBe(1);
      expect(toasts[0]).toEqual(expect.objectContaining({ message: 'Something went wrong', type: 'error' }));
    });
  });

  describe('dismiss', () => {
    it('removes the toast with the given id', () => {
      service.success('First');
      service.success('Second');
      const id = service.toasts()[0].id;
      service.dismiss(id);
      expect(service.toasts().find(t => t.id === id)).toBeUndefined();
      expect(service.toasts().length).toBe(1);
    });
  });

  describe('auto-dismiss', () => {
    beforeEach(() => vi.useFakeTimers());
    afterEach(() => vi.useRealTimers());

    it('removes the toast after 3500 ms', () => {
      service.success('Temporary');
      expect(service.toasts().length).toBe(1);
      vi.advanceTimersByTime(3500);
      expect(service.toasts().length).toBe(0);
    });

    it('keeps the toast before 3500 ms', () => {
      service.success('Temporary');
      vi.advanceTimersByTime(3000);
      expect(service.toasts().length).toBe(1);
    });
  });
});
