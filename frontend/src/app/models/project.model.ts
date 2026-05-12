import type { Stage } from './stage.model';

export interface Project {
  id: number;
  name: string;
  userIds: number[];
  stages: Stage[];
}

export interface ProjectMember {
  id: number;
  userId: number;
  projectId: number;
  role: string;
  joinedAt: string;
}
