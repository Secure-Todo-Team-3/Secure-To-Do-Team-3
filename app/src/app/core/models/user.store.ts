export interface UserStore {
    userGuid: string;
    username: string;
    email: string;
    isActive: boolean;
    isLocked: boolean;
    createdAt: string;
    systemRole: string;
    teamRole?: TeamRoleInfo;
}

export interface TeamRoleInfo {
    teamName: string;
    roleName: string;
} 