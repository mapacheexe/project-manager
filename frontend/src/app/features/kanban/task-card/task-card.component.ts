import { Component, input, output } from '@angular/core';
import { LowerCasePipe } from '@angular/common';
import { Stage, Task } from '../../../models';

@Component({
  selector: 'app-task-card',
  standalone: true,
  imports: [LowerCasePipe],
  templateUrl: './task-card.component.html',
})
export class TaskCardComponent {
  readonly task = input.required<Task>();
  readonly stages = input.required<Stage[]>();
  readonly editRequested = output<Task>();
  readonly deleteRequested = output<Task>();
  readonly taskMoved = output<{ task: Task; targetStageId: number }>();

  protected onStageChange(targetStageId: string): void {
    this.taskMoved.emit({ task: this.task(), targetStageId: Number(targetStageId) });
  }
}
