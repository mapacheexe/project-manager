import { Component, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { toSignal, toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { ProjectService } from '../../services/project.service';
import { ProjectCardComponent } from './project-card/project-card.component';
import { Project } from '../../models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [ProjectCardComponent],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent {
  private readonly auth = inject(AuthService);
  private readonly projectService = inject(ProjectService);
  private readonly router = inject(Router);

  private readonly refresh = signal(0);
  private readonly query = computed(() => ({ userId: this.auth.currentUser()!.id, r: this.refresh() }));
  protected readonly projects = toSignal(
    toObservable(this.query).pipe(switchMap(({ userId }) => this.projectService.getByUser(userId))),
    { initialValue: [] }
  );

  protected readonly showForm = signal(false);
  protected readonly newProjectName = signal('');

  openProject(project: Project): void {
    this.router.navigate(['/projects', project.id]);
  }

  createProject(): void {
    const name = this.newProjectName().trim();
    if (!name) return;
    const userId = this.auth.currentUser()!.id;
    this.projectService.create(userId, { name }).subscribe({
      next: () => {
        this.showForm.set(false);
        this.newProjectName.set('');
        this.refresh.update(n => n + 1);
      },
    });
  }
}
