import { Component, computed, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router, RouterLink } from '@angular/router';
import { UserService } from '../../../services/user.service';

function passwordsMatch(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password')?.value;
  const confirmPassword = control.get('confirmPassword')?.value;
  return password === confirmPassword ? null : { passwordMismatch: true };
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
})
export class RegisterComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);

  protected readonly form = this.formBuilder.nonNullable.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
    confirmPassword: ['', Validators.required],
  }, { validators: passwordsMatch });

  private readonly formValue = toSignal(this.form.valueChanges, { initialValue: this.form.value });

  protected readonly passwordMismatch = computed(() => {
    const { password, confirmPassword } = this.formValue();
    return !!confirmPassword && password !== confirmPassword;
  });

  protected readonly registerError = signal<string | null>(null);

  submit(): void {
    if (this.form.invalid) return;
    this.registerError.set(null);
    const { name, email, password } = this.form.getRawValue();
    this.userService.create({ name, email, password }).subscribe({
      next: () => this.router.navigate(['/login']),
      error: (err) => {
        if (err.status === 409) this.registerError.set('Este email ya está registrado.');
        else if (err.status === 400) this.registerError.set('Los datos introducidos no son válidos.');
        else this.registerError.set('No se pudo completar el registro. Inténtalo de nuevo.');
      },
    });
  }
}
