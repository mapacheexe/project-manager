import { Component, effect, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Stage, Task } from '../../../models';

export interface TaskFormValue {
  title: string;
  description: string;
  status: string | null;
  stageId: number | null;
}

@Component({
  selector: 'app-task-modal',
  imports: [ReactiveFormsModule],
  templateUrl: './task-modal.component.html',
  styleUrl: './task-modal.component.scss',
})
export class TaskModalComponent {
  task = input<Task | null>(null);
  stages = input<Stage[]>([]);
  saved = output<TaskFormValue>();
  cancelled = output();

  private formBuilder = inject(FormBuilder);

  form = this.formBuilder.nonNullable.group({
    title: ['', Validators.required],
    description: [''],
    status: [''],
    stageId: [0 as number],
  });

  readonly statusOptions = ['PENDING', 'IN_PROGRESS', 'DONE'];

  constructor() {
    effect(() => {
      const task = this.task();
      if (task) {
        this.form.patchValue({
          title: task.title,
          description: task.description ?? '',
          status: task.status ?? '',
          stageId: task.stageId,
        });
      } else {
        this.form.reset({ title: '', description: '', status: '', stageId: 0 });
      }
    });
  }

  onSave(): void {
    if (this.form.invalid) return;
    const { title, description, status, stageId } = this.form.getRawValue();
    this.saved.emit({
      title,
      description,
      status: status || null,
      stageId: stageId || null,
    });
  }

  onCancel(): void {
    this.cancelled.emit();
  }
}
