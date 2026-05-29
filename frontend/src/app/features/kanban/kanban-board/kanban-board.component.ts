import { Component, computed, effect, inject, input, numberAttribute, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { of, switchMap } from 'rxjs';
import { ProjectService } from '../../../services/project.service';
import { StageService } from '../../../services/stage.service';
import { TaskService } from '../../../services/task.service';
import { ToastService } from '../../../shared/services/toast.service';
import { Stage, Task } from '../../../models';
import { Router, RouterLink } from '@angular/router';
import { StageColumnComponent } from '../stage-column/stage-column.component';
import { TaskFormValue, TaskModalComponent } from '../../../shared/components/task-modal/task-modal.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { AutofocusDirective } from '../../../shared/directives/autofocus.directive';

@Component({
  selector: 'app-kanban-board',
  standalone: true,
  imports: [RouterLink, StageColumnComponent, TaskModalComponent, ConfirmDialogComponent, AutofocusDirective],
  templateUrl: './kanban-board.component.html',
  styleUrl: './kanban-board.component.scss',
})
export class KanbanBoardComponent {
  private readonly projectService = inject(ProjectService);
  private readonly stageService = inject(StageService);
  private readonly taskService = inject(TaskService);
  private readonly toastService = inject(ToastService);
  private readonly router = inject(Router);

  readonly id = input.required({ transform: numberAttribute });

  private readonly stagesResource = rxResource({
    params: () => this.id(),
    stream: ({ params: id }) => this.stageService.getByProject(id),
  });
  private readonly projectResource = rxResource({
    params: () => this.id(),
    stream: ({ params: id }) => this.projectService.getById(id),
  });

  protected readonly stages = computed(() => this.stagesResource.value() ?? []);
  protected readonly project = this.projectResource.value;

  constructor() {
    effect(() => {
      if (this.projectResource.error()) {
        this.router.navigate(['/dashboard']);
      }
    });
  }

  protected readonly showStageForm = signal(false);
  protected readonly newStageName = signal('');
  protected readonly editingTask = signal<Task | null>(null);
  protected readonly deletingTask = signal<Task | null>(null);
  protected readonly creatingInStageId = signal<number | null>(null);
  protected readonly deletingStageId = signal<number | null>(null);

  private reload(): void {
    this.stagesResource.reload();
  }

  protected createStage(): void {
    const name = this.newStageName().trim();
    if (!name) return;
    this.stageService.create(this.id(), { name }).subscribe({
      next: () => {
        this.showStageForm.set(false);
        this.newStageName.set('');
        this.reload();
        this.toastService.success('Etapa creada');
      },
      error: () => this.toastService.error('No se pudo crear la etapa'),
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

  protected onTaskMovedLeft(task: Task): void {
    this.shiftTask(task, -1);
  }

  protected onTaskMovedRight(task: Task): void {
    this.shiftTask(task, 1);
  }

  private shiftTask(task: Task, delta: number): void {
    const stages = this.stages();
    const index = stages.findIndex(s => s.id === task.stageId);
    const target = stages[index + delta];
    if (!target) return;
    this.taskService.move(task.id, { stageId: target.id, position: 0 }).subscribe({
      next: () => this.reload(),
      error: () => this.toastService.error('No se pudo mover la tarea'),
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

    const payload = this.swapStagesAndAssignPositions(stages, index, targetIndex);
    this.stageService.reorder(this.id(), payload).subscribe({
      next: () => this.reload(),
      error: () => this.toastService.error('No se pudo reordenar la etapa'),
    });
  }

  private swapStagesAndAssignPositions(stages: Stage[], fromIndex: number, toIndex: number) {
    const reordered = [...stages];
    [reordered[fromIndex], reordered[toIndex]] = [reordered[toIndex], reordered[fromIndex]];
    return reordered.map((stage, position) => ({ id: stage.id, position }));
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
    const stageId = this.creatingInStageId();
    if (editTask) this.saveEditedTask(editTask, value);
    else if (stageId) this.saveNewTask(stageId, value);
  }

  private saveEditedTask(task: Task, value: TaskFormValue): void {
    const stageChanged = value.stageId !== null && value.stageId !== task.stageId;
    this.taskService.update(task.id, {
      title: value.title,
      description: value.description,
      status: value.status ?? undefined,
    }).pipe(
      switchMap(() => stageChanged
        ? this.taskService.move(task.id, { stageId: value.stageId!, position: 0 })
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
  }

  private saveNewTask(stageId: number, value: TaskFormValue): void {
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
