import { Component, computed, inject, input, signal } from '@angular/core';
import { numberAttribute } from '@angular/core';
import { toSignal, toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { Router, RouterLink } from '@angular/router';
import { ProjectService } from '../../services/project.service';
import { UserService } from '../../services/user.service';
import { ToastService } from '../../shared/services/toast.service';
import { AuthService } from '../../core/auth/auth.service';
import { accentColor } from '../../shared/utils/accent-color';

const ASSIGNABLE_ROLES = ['ADMIN', 'MEMBER'] as const;

@Component({
  selector: 'app-project-members',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './project-members.component.html',
  styleUrl: './project-members.component.scss',
})
export class ProjectMembersComponent {
  private readonly projectService = inject(ProjectService);
  private readonly userService = inject(UserService);
  private readonly toastService = inject(ToastService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly currentUserId = computed(() => this.authService.currentUserId());

  readonly id = input.required({ transform: numberAttribute });

  protected readonly assignableRoles = ASSIGNABLE_ROLES;

  private readonly refresh = signal(0);
  private readonly members$ = toObservable(
    computed(() => ({ id: this.id(), r: this.refresh() }))
  ).pipe(switchMap(({ id }) => this.projectService.getMembers(id)));

  protected readonly members = toSignal(this.members$, { initialValue: [] });
  protected readonly allUsers = toSignal(this.userService.getAll(), { initialValue: [] });

  protected readonly enrichedMembers = computed(() => {
    const userMap = new Map(this.allUsers().map(u => [u.id, u]));
    return this.members().map(m => ({
      ...m,
      user: userMap.get(m.userId),
      avatarColor: accentColor(m.userId),
    }));
  });

  protected readonly availableUsers = computed(() => {
    const memberIds = new Set(this.members().map(m => m.userId));
    return this.allUsers().filter(u => !memberIds.has(u.id));
  });

  protected readonly selectedUserId = signal<number | null>(null);

  protected readonly isOwner = computed(() =>
    this.members().some(m => m.userId === this.currentUserId() && m.role === 'OWNER')
  );

  private reload(): void {
    this.refresh.update(n => n + 1);
  }

  protected addMember(): void {
    const userId = this.selectedUserId();
    if (!userId) return;
    this.projectService.addMember(this.id(), userId).subscribe({
      next: () => {
        this.selectedUserId.set(null);
        this.reload();
        this.toastService.success('Miembro añadido');
      },
      error: () => this.toastService.error('No se pudo añadir el miembro'),
    });
  }

  protected updateRole(userId: number, role: string): void {
    this.projectService.updateMember(this.id(), userId, { role }).subscribe({
      next: () => this.reload(),
      error: () => this.toastService.error('No se pudo actualizar el rol'),
    });
  }

  protected removeMember(userId: number): void {
    this.projectService.removeMember(this.id(), userId).subscribe({
      next: () => {
        this.reload();
        this.toastService.success('Miembro eliminado');
      },
      error: () => this.toastService.error('No se pudo eliminar el miembro'),
    });
  }

  protected leaveProject(): void {
    this.projectService.leaveProject(this.id()).subscribe({
      next: () => {
        this.toastService.success('Has abandonado el proyecto');
        this.router.navigate(['/dashboard']);
      },
      error: () => this.toastService.error('No se pudo abandonar el proyecto'),
    });
  }
}
