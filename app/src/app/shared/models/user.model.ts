import { Role } from "./role.model";

export interface User {
  userGuid: string;
  username: string;
  role?: Role;
  email?: string;
}