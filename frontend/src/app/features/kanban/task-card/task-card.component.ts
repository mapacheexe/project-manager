import { Component, computed, input, output } from '@angular/core';
import { LowerCasePipe } from '@angular/common';
import { Task } from '../../../models';

const STATUS_LABELS: Record<string, string> = {
  PENDING: 'Pendiente',
  IN_PROGRESS: 'En progreso',
  DONE: 'Hecho',
};

const STATUS_CYCLE: Record<string, string> = {
  PENDING: 'IN_PROGRESS',
  IN_PROGRESS: 'DONE',
  DONE: 'PENDING',
};

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
  readonly statusChanged = output<{ task: Task; status: string }>();

  readonly statusLabel = computed(() => STATUS_LABELS[this.task().status] ?? this.task().status);

  protected cycleStatus(): void {
    const next = STATUS_CYCLE[this.task().status] ?? 'PENDING';
    this.statusChanged.emit({ task: this.task(), status: next });
  }
}
