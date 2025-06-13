export interface UserResponseDto {
    id: number;
    userGuid: string; 
    username: string;
    email: string;
    systemRole: string;
    isLocked: boolean;
    isActive: boolean;
}