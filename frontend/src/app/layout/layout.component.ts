import { Component, computed, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';
import { UserService } from '../services/user.service';
import { ToastService } from '../shared/services/toast.service';
import { ToastComponent } from '../shared/components/toast/toast.component';
import { ProfileModalComponent } from '../shared/components/profile-modal/profile-modal.component';
import { ConfirmDialogComponent } from '../shared/components/confirm-dialog/confirm-dialog.component';
import { accentColor } from '../shared/utils/accent-color';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, ToastComponent, ProfileModalComponent, ConfirmDialogComponent],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss',
})
export class LayoutComponent {
  private readonly authService = inject(AuthService);
  private readonly userService = inject(UserService);
  private readonly toastService = inject(ToastService);
  private readonly router = inject(Router);

  protected readonly userName = computed(() => this.authService.currentUser()?.name ?? '');
  protected readonly avatarInitial = computed(() => this.authService.currentUser()?.name?.charAt(0)?.toUpperCase() ?? '?');
  protected readonly avatarColor = computed(() => accentColor(this.authService.currentUser()?.id ?? 0));
  protected readonly showProfileModal = signal(false);
  protected readonly showDeleteConfirm = signal(false);

  protected openProfile(): void {
    this.showProfileModal.set(true);
  }

  protected onNameSaved(name: string): void {
    const user = this.authService.currentUser();
    if (!user) return;
    this.userService.update(user.id, { name, email: user.email }).subscribe({
      next: updated => {
        this.authService.updateCurrentUser(updated);
        this.showProfileModal.set(false);
        this.toastService.success('Nombre actualizado');
      },
      error: () => this.toastService.error('No se pudo actualizar el nombre'),
    });
  }

  protected onDeleteRequested(): void {
    this.showProfileModal.set(false);
    this.showDeleteConfirm.set(true);
  }

  protected deleteAccount(): void {
    const user = this.authService.currentUser();
    if (!user) return;
    this.userService.delete(user.id).subscribe({
      next: () => {
        this.authService.logout();
        this.router.navigate(['/login']);
        this.toastService.success('Cuenta eliminada');
      },
      error: () => this.toastService.error('No se pudo eliminar la cuenta'),
    });
  }

  protected logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
