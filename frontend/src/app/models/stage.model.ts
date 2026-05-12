import type { Task } from './task.model';

export interface Stage {
  id: number;
  name: string;
  projectId: number;
  position: number;
  tasks: Task[];
}
