import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
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
  private projectService = inject(ProjectService);
  private router = inject(Router);

  protected readonly projects = toSignal(this.projectService.getAll(), { initialValue: [] });
  protected readonly showForm = signal(false);
  protected readonly newProjectName = signal('');

  openProject(project: Project): void {
    this.router.navigate(['/projects', project.id]);
  }

  createProject(): void {
    const name = this.newProjectName().trim();
    if (!name) return;
    this.projectService.create({ name }).subscribe({
      next: () => {
        this.showForm.set(false);
        this.newProjectName.set('');
      },
    });
  }
}
