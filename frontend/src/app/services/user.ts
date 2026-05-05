import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class User {

  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080';

  getUserById(id: number) {
    return this.http.get(`${this.baseUrl}/users/${id}`);
  }

  getAllUsers() {
    return this.http.get(`${this.baseUrl}/users`);
  }

}
