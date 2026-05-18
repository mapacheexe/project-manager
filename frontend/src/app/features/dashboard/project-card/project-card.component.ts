import { Component, computed, input, output } from '@angular/core';
import { Project } from '../../../models';
import { accentColor } from '../../../shared/utils/accent-color';

@Component({
  selector: 'app-project-card',
  standalone: true,
  templateUrl: './project-card.component.html',
  styleUrl: './project-card.component.scss',
})
export class ProjectCardComponent {
  readonly project = input.required<Project>();
  readonly selected = output<Project>();
  readonly deleteRequested = output<Project>();

  readonly accentColor = computed(() => accentColor(this.project().id));
}
