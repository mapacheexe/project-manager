import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { AuthService } from '../../../core/auth/auth.service';
import { UserService } from '../../../services/user.service';
import { User } from '../../../models';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  private auth = inject(AuthService);
  private userService = inject(UserService);
  private router = inject(Router);

  protected readonly users = toSignal(this.userService.getAll(), { initialValue: [] });

  selectUser(user: User): void {
    this.auth.login(user);
    this.router.navigate(['/dashboard']);
  }
}
