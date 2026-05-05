import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { User } from './services/user';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {

  private userService = inject(User);

  user = signal<any>(null);

  fetchUser() {
    this.userService.getUserById(1).subscribe({
      next: (data) => this.user.set(data),
      error: (err) => console.error(err)
    })
  }
}
