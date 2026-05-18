import { Component, computed, input, output } from '@angular/core';
import { Project } from '../../../models';

const ACCENT_COLORS = ['#6366f1', '#f59e0b', '#10b981', '#ef4444', '#8b5cf6', '#06b6d4'];

@Component({
  selector: 'app-project-card',
  standalone: true,
  templateUrl: './project-card.component.html',
})
export class ProjectCardComponent {
  readonly project = input.required<Project>();
  readonly selected = output<Project>();
  readonly deleteRequested = output<Project>();

  readonly accentColor = computed(() => ACCENT_COLORS[this.project().id % ACCENT_COLORS.length]);
}
