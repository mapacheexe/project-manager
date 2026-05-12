import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project, ProjectMember } from '../models';
import { environment } from '../../environments/environment';

export interface CreateProjectPayload {
  name: string;
}

@Injectable({ providedIn: 'root' })
export class ProjectService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/projects`;

  getAll(): Observable<Project[]> {
    return this.http.get<Project[]>(this.baseUrl);
  }

  getById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.baseUrl}/${id}`);
  }

  create(payload: CreateProjectPayload): Observable<Project> {
    return this.http.post<Project>(this.baseUrl, payload);
  }

  update(id: number, payload: CreateProjectPayload): Observable<Project> {
    return this.http.patch<Project>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getMembers(projectId: number): Observable<ProjectMember[]> {
    return this.http.get<ProjectMember[]>(`${this.baseUrl}/${projectId}/users`);
  }

  addMember(projectId: number, userId: number): Observable<ProjectMember> {
    return this.http.post<ProjectMember>(`${this.baseUrl}/${projectId}/users/${userId}`, {});
  }

  updateMember(projectId: number, userId: number, payload: Pick<ProjectMember, 'role'>): Observable<ProjectMember> {
    return this.http.patch<ProjectMember>(`${this.baseUrl}/${projectId}/users/${userId}`, payload);
  }

  removeMember(projectId: number, userId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${projectId}/users/${userId}`);
  }
}
