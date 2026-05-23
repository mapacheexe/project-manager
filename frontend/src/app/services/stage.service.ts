import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Stage } from '../models';
import { environment } from '../../environments/environment';

export interface CreateStagePayload {
  name: string;
}

@Injectable({ providedIn: 'root' })
export class StageService {
  private readonly httpClient = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  getByProject(projectId: number): Observable<Stage[]> {
    return this.httpClient.get<Stage[]>(`${this.apiUrl}/projects/${projectId}/stages`);
  }

  create(projectId: number, payload: CreateStagePayload): Observable<Stage> {
    return this.httpClient.post<Stage>(`${this.apiUrl}/projects/${projectId}/stages`, payload);
  }

  update(id: number, payload: CreateStagePayload): Observable<Stage> {
    return this.httpClient.patch<Stage>(`${this.apiUrl}/stages/${id}`, payload);
  }

  reorder(projectId: number, stages: Pick<Stage, 'id' | 'position'>[]): Observable<Stage[]> {
    return this.httpClient.patch<Stage[]>(`${this.apiUrl}/projects/${projectId}/stages/reorder`, stages);
  }

  delete(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.apiUrl}/stages/${id}`);
  }
}
