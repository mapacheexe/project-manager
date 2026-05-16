import { Component, effect, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Task } from '../../../models';

export interface TaskFormValue {
  title: string;
  description: string;
  status: string | null;
}

@Component({
  selector: 'app-task-modal',
  imports: [ReactiveFormsModule],
  templateUrl: './task-modal.component.html',
  styleUrl: './task-modal.component.scss',
})
export class TaskModalComponent {
  task = input<Task | null>(null);
  saved = output<TaskFormValue>();
  cancelled = output();

  private formBuilder = inject(FormBuilder);

  form = this.formBuilder.nonNullable.group({
    title: ['', Validators.required],
    description: [''],
    status: [''],
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
        });
      } else {
        this.form.reset({ title: '', description: '', status: '' });
      }
    });
  }

  onSave(): void {
    if (this.form.invalid) return;
    const raw = this.form.getRawValue();
    this.saved.emit({ ...raw, status: raw.status || null });
  }

  onCancel(): void {
    this.cancelled.emit();
  }
}
