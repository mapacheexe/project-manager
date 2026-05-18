import { Component, computed, input, output } from '@angular/core';
import { Stage, Task } from '../../../models';
import { TaskCardComponent } from '../task-card/task-card.component';

const ACCENT_COLORS = ['#6366f1', '#f59e0b', '#10b981', '#ef4444', '#8b5cf6', '#06b6d4'];

@Component({
  selector: 'app-stage-column',
  standalone: true,
  imports: [TaskCardComponent],
  templateUrl: './stage-column.component.html',
})
export class StageColumnComponent {
  readonly stage = input.required<Stage>();
  readonly stages = input.required<Stage[]>();

  readonly accentColor = computed(() => ACCENT_COLORS[this.stage().id % ACCENT_COLORS.length]);
  readonly stageDeleted = output<number>();
  readonly taskCreateRequested = output<number>();
  readonly taskMoved = output<{ task: Task; targetStageId: number }>();
  readonly taskStatusChanged = output<{ task: Task; status: string }>();
  readonly taskEditRequested = output<Task>();
  readonly taskDeleteRequested = output<Task>();
}
