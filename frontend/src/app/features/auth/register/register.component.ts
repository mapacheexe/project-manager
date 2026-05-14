import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private router = inject(Router);

  protected readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });
  protected readonly registerError = signal<string | null>(null);

  submit(): void {
    if (this.form.invalid) return;
    this.registerError.set(null);
    this.userService.create(this.form.getRawValue()).subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.registerError.set('Este email ya está registrado.'),
    });
  }
}
