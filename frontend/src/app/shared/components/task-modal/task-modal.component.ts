import { Component, effect, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Task } from '../../../models';

export interface TaskFormValue {
  title: string;
  description: string;
  status: string;
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
    status: ['PENDING', Validators.required],
  });

  readonly statusOptions = ['PENDING', 'IN_PROGRESS', 'DONE'];

  constructor() {
    effect(() => {
      const task = this.task();
      if (task) {
        this.form.patchValue({
          title: task.title,
          description: task.description ?? '',
          status: task.status,
        });
      } else {
        this.form.reset({ title: '', description: '', status: 'PENDING' });
      }
    });
  }

  onSave(): void {
    if (this.form.invalid) return;
    this.saved.emit(this.form.getRawValue());
  }

  onCancel(): void {
    this.cancelled.emit();
  }
}
