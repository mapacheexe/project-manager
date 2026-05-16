import { Component, computed, inject, input, signal } from '@angular/core';
import { numberAttribute } from '@angular/core';
import { toSignal, toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { ProjectService } from '../../../services/project.service';
import { StageService } from '../../../services/stage.service';
import { TaskService } from '../../../services/task.service';
import { Task } from '../../../models';
import { RouterLink } from '@angular/router';
import { StageColumnComponent } from '../stage-column/stage-column.component';
import { TaskFormValue, TaskModalComponent } from '../../../shared/components/task-modal/task-modal.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-kanban-board',
  standalone: true,
  imports: [RouterLink, StageColumnComponent, TaskModalComponent, ConfirmDialogComponent],
  templateUrl: './kanban-board.component.html',
})
export class KanbanBoardComponent {
  private readonly projectService = inject(ProjectService);
  private readonly stageService = inject(StageService);
  private readonly taskService = inject(TaskService);
  protected readonly editingTask = signal<Task | null>(null);
  protected readonly deletingTask = signal<Task | null>(null);

  readonly id = input.required({ transform: numberAttribute });
  private readonly refresh = signal(0);

  private readonly query = computed(() => ({ id: this.id(), r: this.refresh() }));
  private readonly stages$ = toObservable(this.query).pipe(
    switchMap(({ id }) => this.stageService.getByProject(id))
  );
  protected readonly stages = toSignal(this.stages$, { initialValue: [] });

  protected readonly project = toSignal(
    toObservable(this.id).pipe(switchMap(id => this.projectService.getById(id)))
  );

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

  protected onTaskMoved({task, targetStageId}: { task: Task, targetStageId: number }): void {
    this.taskService.move(task.id, { stageId: targetStageId, position: 0 }).subscribe({
      next: () => this.reload(),
    });
  }

  protected onStageDeleted(stageId: number): void {
    this.stageService.delete(stageId).subscribe({
      next: () => this.reload(),
    });
  }

  protected onTaskEditingRequested(task: Task): void {
    this.editingTask.set(task);
  }

  protected onTaskDeleteRequested(task: Task): void {
    this.deletingTask.set(task);
  }

  protected onTaskSaved(value: TaskFormValue) {
    const task = this.editingTask();
    if (!task) return;
    this.taskService.update(task.id, value).subscribe(() => {
      this.editingTask.set(null);
      this.reload();
    })
  }

  protected onTaskDeleteConfirmed(): void {
    const task = this.deletingTask();
    if (!task) return;
    this.taskService.delete(task.id).subscribe({
      next: () => {
        this.deletingTask.set(null);
        this.reload();
      }
    })
  }
}
