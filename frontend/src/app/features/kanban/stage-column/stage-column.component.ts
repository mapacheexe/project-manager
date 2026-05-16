import { Component, input, output, signal } from '@angular/core';
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
  readonly taskCreated = output<{ stageId: number; title: string }>();
  readonly stageDeleted = output<number>();
  readonly stages = input.required<Stage[]>();
  readonly taskMoved = output<{ task: Task; targetStageId: number }>();
  readonly taskEditRequested = output<Task>();
  readonly taskDeleteRequested = output<Task>();
  readonly taskdeletedRequest = output<Task>();

  protected readonly showTaskForm = signal(false);
  protected readonly newTaskTitle = signal('');

  protected submitTask(): void {
    const title = this.newTaskTitle().trim();
    if (!title) return;
    this.taskCreated.emit({ stageId: this.stage().id, title });
    this.newTaskTitle.set('');
    this.showTaskForm.set(false);
  }
}
