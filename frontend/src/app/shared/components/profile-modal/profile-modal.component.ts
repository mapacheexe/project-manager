import { Component, computed, effect, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-profile-modal',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './profile-modal.component.html',
  styleUrl: './profile-modal.component.scss',
})
export class ProfileModalComponent {
  readonly currentName = input.required<string>();

  readonly saved = output<string>();
  readonly cancelled = output<void>();
  readonly deleteRequested = output<void>();

  protected readonly name = signal('');
  protected readonly avatarInitial = computed(() => this.firstLetter(this.name()));

  private firstLetter(name: string): string {
    return name.trim()[0]?.toUpperCase() ?? '?';
  }

  constructor() {
    effect(() => this.name.set(this.currentName()));
  }

  protected submit(): void {
    const trimmed = this.name().trim();
    if (trimmed) this.saved.emit(trimmed);
  }
}
