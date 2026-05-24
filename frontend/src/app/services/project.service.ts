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
  private readonly httpClient = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/projects`;

  getAll(): Observable<Project[]> {
    return this.httpClient.get<Project[]>(this.baseUrl);
  }

  getByUser(userId: number): Observable<Project[]> {
    return this.httpClient.get<Project[]>(`${environment.apiUrl}/users/${userId}/projects`);
  }

  getById(id: number): Observable<Project> {
    return this.httpClient.get<Project>(`${this.baseUrl}/${id}`);
  }

  create(userId: number, payload: CreateProjectPayload): Observable<Project> {
    return this.httpClient.post<Project>(`${environment.apiUrl}/users/${userId}/projects`, payload);
  }

  update(id: number, payload: CreateProjectPayload): Observable<Project> {
    return this.httpClient.patch<Project>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/${id}`);
  }

  getMembers(projectId: number): Observable<ProjectMember[]> {
    return this.httpClient.get<ProjectMember[]>(`${this.baseUrl}/${projectId}/users`);
  }

  addMember(projectId: number, userId: number): Observable<ProjectMember> {
    return this.httpClient.post<ProjectMember>(`${this.baseUrl}/${projectId}/users/${userId}`, {});
  }

  updateMember(projectId: number, userId: number, payload: Pick<ProjectMember, 'role'>): Observable<ProjectMember> {
    return this.httpClient.patch<ProjectMember>(`${this.baseUrl}/${projectId}/users/${userId}`, payload);
  }

  removeMember(projectId: number, userId: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/${projectId}/users/${userId}`);
  }

  leaveProject(projectId: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/${projectId}/users/me`);
  }
}
