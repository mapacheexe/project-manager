import { Component, input, output } from '@angular/core';
import { Project } from '../../../models';

@Component({
  selector: 'app-project-card',
  standalone: true,
  templateUrl: './project-card.component.html',
})
export class ProjectCardComponent {
  readonly project = input.required<Project>();
  readonly selected = output<Project>();
}
