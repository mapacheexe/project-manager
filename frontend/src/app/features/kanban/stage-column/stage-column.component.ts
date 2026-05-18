import { Component, computed, input, output } from '@angular/core';
import { Stage, Task } from '../../../models';
import { TaskCardComponent } from '../task-card/task-card.component';
import { accentColor } from '../../../shared/utils/accent-color';

@Component({
  selector: 'app-stage-column',
  standalone: true,
  imports: [TaskCardComponent],
  templateUrl: './stage-column.component.html',
})
export class StageColumnComponent {
  readonly stage = input.required<Stage>();

  readonly accentColor = computed(() => accentColor(this.stage().id));
  readonly stageDeleted = output<number>();
  readonly taskCreateRequested = output<number>();
  readonly taskStatusChanged = output<{ task: Task; status: string }>();
  readonly taskEditRequested = output<Task>();
  readonly taskDeleteRequested = output<Task>();
}
