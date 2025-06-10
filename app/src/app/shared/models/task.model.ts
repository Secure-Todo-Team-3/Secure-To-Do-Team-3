export interface Task {
  taskGuid?: string;
  name: string;
  description: string;
  dueDate: string;
  teamName: string;
  teamId: number;
  assignedToMyself: boolean;
  assignedToUsername?: string;
  currentStatusName: string;
  currentStatusId: number;
}