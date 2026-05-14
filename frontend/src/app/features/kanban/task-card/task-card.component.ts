import { Component, input, output } from '@angular/core';
import { LowerCasePipe } from '@angular/common';
import { Task } from '../../../models';

@Component({
  selector: 'app-task-card',
  standalone: true,
  imports: [LowerCasePipe],
  templateUrl: './task-card.component.html',
})
export class TaskCardComponent {
  readonly task = input.required<Task>();
  readonly editRequested = output<Task>();
  readonly deleteRequested = output<Task>();
}
