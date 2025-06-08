export interface Task {
  id?: string;
  title: string;
  description: string;
  dueDate: string;
  team: string;
  assignToMyself: boolean;
  status: string;
}