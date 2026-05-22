import { Component, computed, inject } from '@angular/core';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.scss',
})
export class ToastComponent {
  private readonly toastService = inject(ToastService);
  protected readonly toasts = computed(() => this.toastService.toasts());
  protected dismiss(id: number): void { this.toastService.dismiss(id); }
}
