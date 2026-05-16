import { Component, input, output } from '@angular/core';
import { Stage, Task } from '../../../models';
import { TaskCardComponent } from '../task-card/task-card.component';

@Component({
  selector: 'app-stage-column',
  standalone: true,
  imports: [TaskCardComponent],
  templateUrl: './stage-column.component.html',
})
export class StageColumnComponent {
  readonly stage = input.required<Stage>();
  readonly stages = input.required<Stage[]>();
  readonly stageDeleted = output<number>();
  readonly taskCreateRequested = output<number>();
  readonly taskMoved = output<{ task: Task; targetStageId: number }>();
  readonly taskEditRequested = output<Task>();
  readonly taskDeleteRequested = output<Task>();
}
