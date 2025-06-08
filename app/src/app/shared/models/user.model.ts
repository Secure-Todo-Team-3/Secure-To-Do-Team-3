import { Role } from "./role.model";

export interface User {
  id: string;
  username: string;
  role?: Role;
}