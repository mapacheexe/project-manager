import { Component, computed, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss',
})
export class LayoutComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  protected readonly userName = computed(() => this.auth.currentUser()?.name ?? '');

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
