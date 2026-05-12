import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task } from '../models';
import { environment } from '../../environments/environment';

export interface CreateTaskPayload {
  title: string;
  description: string;
}

export interface UpdateTaskPayload {
  title?: string;
  description?: string;
  status?: string;
}

export interface MoveTaskPayload {
  stageId: number;
  position: number;
}

@Injectable({ providedIn: 'root' })
export class TaskService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  getByStage(stageId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/stages/${stageId}/tasks`);
  }

  getById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/tasks/${id}`);
  }

  create(stageId: number, payload: CreateTaskPayload): Observable<Task> {
    return this.http.post<Task>(`${this.apiUrl}/stages/${stageId}/tasks`, payload);
  }

  update(id: number, payload: UpdateTaskPayload): Observable<Task> {
    return this.http.patch<Task>(`${this.apiUrl}/tasks/${id}`, payload);
  }

  move(id: number, payload: MoveTaskPayload): Observable<Task> {
    return this.http.patch<Task>(`${this.apiUrl}/tasks/${id}/move`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/tasks/${id}`);
  }
}
