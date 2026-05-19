import { Component, computed, input, output, signal } from '@angular/core';
import { Stage, Task } from '../../../models';
import { TaskCardComponent } from '../task-card/task-card.component';
import { accentColor } from '../../../shared/utils/accent-color';

@Component({
  selector: 'app-stage-column',
  standalone: true,
  imports: [TaskCardComponent],
  templateUrl: './stage-column.component.html',
  styleUrl: './stage-column.component.scss',
})
export class StageColumnComponent {
  readonly stage = input.required<Stage>();
  readonly isFirst = input<boolean>(false);
  readonly isLast = input<boolean>(false);

  readonly accentColor = computed(() => accentColor(this.stage().id));
  readonly stageDeleted = output<number>();
  readonly stageRenamed = output<{ id: number; name: string }>();
  readonly stageMovedLeft = output<number>();
  readonly stageMovedRight = output<number>();
  readonly taskCreateRequested = output<number>();
  readonly taskStatusChanged = output<{ task: Task; status: string }>();
  readonly taskEditRequested = output<Task>();
  readonly taskDeleteRequested = output<Task>();

  protected readonly editingName = signal(false);
  protected readonly pendingName = signal('');

  protected startEditing(): void {
    this.pendingName.set(this.stage().name);
    this.editingName.set(true);
  }

  protected confirmRename(): void {
    const name = this.pendingName().trim();
    if (name && name !== this.stage().name) {
      this.stageRenamed.emit({ id: this.stage().id, name });
    }
    this.editingName.set(false);
  }

  protected cancelRename(): void {
    this.editingName.set(false);
  }
}
