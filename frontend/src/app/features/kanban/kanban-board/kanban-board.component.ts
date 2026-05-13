import { Component, computed, inject, input, signal } from '@angular/core';
import { numberAttribute } from '@angular/core';
import { toSignal, toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { StageService } from '../../../services/stage.service';
import { TaskService } from '../../../services/task.service';
import { StageColumnComponent } from '../stage-column/stage-column.component';

@Component({
  selector: 'app-kanban-board',
  standalone: true,
  imports: [StageColumnComponent],
  templateUrl: './kanban-board.component.html',
})
export class KanbanBoardComponent {
  private readonly stageService = inject(StageService);
  private readonly taskService = inject(TaskService);

  readonly id = input.required({ transform: numberAttribute });
  private readonly refresh = signal(0);

  private readonly query = computed(() => ({ id: this.id(), r: this.refresh() }));
  private readonly stages$ = toObservable(this.query).pipe(
    switchMap(({ id }) => this.stageService.getByProject(id))
  );
  protected readonly stages = toSignal(this.stages$, { initialValue: [] });

  protected readonly showStageForm = signal(false);
  protected readonly newStageName = signal('');

  private reload(): void {
    this.refresh.update(n => n + 1);
  }

  protected createStage(): void {
    const name = this.newStageName().trim();
    if (!name) return;
    this.stageService.create(this.id(), { name }).subscribe({
      next: () => {
        this.showStageForm.set(false);
        this.newStageName.set('');
        this.reload();
      },
    });
  }

  protected onTaskCreated(stageId: number, title: string): void {
    this.taskService.create(stageId, { title, description: '' }).subscribe({
      next: () => this.reload(),
    });
  }

  protected onStageDeleted(stageId: number): void {
    this.stageService.delete(stageId).subscribe({
      next: () => this.reload(),
    });
  }
}
