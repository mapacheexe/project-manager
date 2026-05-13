import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  protected readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });
  protected readonly loginError = signal(false);

  submit(): void {
    if (this.form.invalid) return;
    this.loginError.set(false);
    const { email } = this.form.getRawValue();
    this.userService.getAll().subscribe({
      next: (users) => {
        const user = users.find(u => u.email === email);
        if (user) {
          this.auth.login(user);
          this.router.navigate(['/dashboard']);
        } else {
          this.loginError.set(true);
        }
      },
    });
  }
}
