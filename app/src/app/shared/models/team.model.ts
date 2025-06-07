import { Task } from './task.model';
import { User } from './user.model';

export interface Team {
  isOwner: boolean;
  members: User[];
  tasks: Task[];
  id: string;
  name: string;
  description: string;
  status: string;
}
