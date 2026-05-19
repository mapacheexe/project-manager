import { Component, computed, inject, input, signal } from '@angular/core';
import { numberAttribute } from '@angular/core';
import { toSignal, toObservable } from '@angular/core/rxjs-interop';
import { of, switchMap } from 'rxjs';
import { ProjectService } from '../../../services/project.service';
import { StageService } from '../../../services/stage.service';
import { TaskService } from '../../../services/task.service';
import { ToastService } from '../../../shared/services/toast.service';
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
  styleUrl: './kanban-board.component.scss',
})
export class KanbanBoardComponent {
  private readonly projectService = inject(ProjectService);
  private readonly stageService = inject(StageService);
  private readonly taskService = inject(TaskService);
  private readonly toastService = inject(ToastService);

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
  protected readonly editingTask = signal<Task | null>(null);
  protected readonly deletingTask = signal<Task | null>(null);
  protected readonly creatingInStageId = signal<number | null>(null);
  protected readonly deletingStageId = signal<number | null>(null);

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

  protected onTaskCreateRequested(stageId: number): void {
    this.creatingInStageId.set(stageId);
  }

  protected onTaskStatusChanged({ task, status }: { task: Task; status: string }): void {
    this.taskService.update(task.id, {
      title: task.title,
      description: task.description,
      status,
    }).subscribe({
      next: () => this.reload(),
      error: () => this.toastService.error('No se pudo actualizar el estado'),
    });
  }

  protected onStageMovedLeft(stageId: number): void {
    this.shiftStage(stageId, -1);
  }

  protected onStageMovedRight(stageId: number): void {
    this.shiftStage(stageId, 1);
  }

  private shiftStage(stageId: number, delta: number): void {
    const stages = this.stages();
    const index = stages.findIndex(s => s.id === stageId);
    const targetIndex = index + delta;
    if (targetIndex < 0 || targetIndex >= stages.length) return;

    const swapped = [...stages];
    [swapped[index], swapped[targetIndex]] = [swapped[targetIndex], swapped[index]];
    const payload = swapped.map((s, i) => ({ id: s.id, position: i }));

    this.stageService.reorder(this.id(), payload).subscribe({
      next: () => this.reload(),
      error: () => this.toastService.error('No se pudo reordenar la etapa'),
    });
  }

  protected onStageRenamed({ id, name }: { id: number; name: string }): void {
    this.stageService.update(id, { name }).subscribe({
      next: () => {
        this.reload();
        this.toastService.success('Etapa renombrada');
      },
      error: () => this.toastService.error('No se pudo renombrar la etapa'),
    });
  }

  protected onStageDeleted(stageId: number): void {
    this.deletingStageId.set(stageId);
  }

  protected onStageDeleteConfirmed(): void {
    const id = this.deletingStageId();
    if (!id) return;
    this.stageService.delete(id).subscribe({
      next: () => {
        this.deletingStageId.set(null);
        this.reload();
        this.toastService.success('Etapa eliminada');
      },
      error: () => this.toastService.error('No se pudo eliminar la etapa'),
    });
  }

  protected onTaskEditRequested(task: Task): void {
    this.editingTask.set(task);
  }

  protected onTaskDeleteRequested(task: Task): void {
    this.deletingTask.set(task);
  }

  protected onTaskSaved(value: TaskFormValue): void {
    const editTask = this.editingTask();
    if (editTask) {
      const stageChanged = value.stageId !== null && value.stageId !== editTask.stageId;
      this.taskService.update(editTask.id, {
        title: value.title,
        description: value.description,
        status: value.status ?? undefined,
      }).pipe(
        switchMap(() => stageChanged
          ? this.taskService.move(editTask.id, { stageId: value.stageId!, position: 0 })
          : of(null)
        )
      ).subscribe({
        next: () => {
          this.editingTask.set(null);
          this.reload();
          this.toastService.success('Tarea actualizada');
        },
        error: () => this.toastService.error('No se pudo actualizar la tarea'),
      });
      return;
    }
    const stageId = this.creatingInStageId();
    if (!stageId) return;
    this.taskService.create(stageId, {
      title: value.title,
      description: value.description,
      status: value.status ?? undefined,
    }).subscribe({
      next: () => {
        this.creatingInStageId.set(null);
        this.reload();
        this.toastService.success('Tarea creada');
      },
      error: () => this.toastService.error('No se pudo crear la tarea'),
    });
  }

  protected onTaskDeleteConfirmed(): void {
    const task = this.deletingTask();
    if (!task) return;
    this.taskService.delete(task.id).subscribe({
      next: () => {
        this.deletingTask.set(null);
        this.reload();
        this.toastService.success('Tarea eliminada');
      },
      error: () => this.toastService.error('No se pudo eliminar la tarea'),
    });
  }
}
