
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from 'src/app/shared/services/api.service'; 
import { Page } from 'src/app/shared/models/page.model'; 
import { UserResponseDto } from 'src/app/shared/models/user-response.dto';
import { UpdateUserSystemRoleRequestDto } from 'src/app/shared/models/update-user-system-role-request.dto';

@Injectable({
  providedIn: 'root'
})
export class AdminUserManagementService {
  private readonly baseUrl = 'admin/management/users';

  constructor(private api: ApiService) { }

  /**
   * Searches for users across the system.
   * @param searchTerm Optional search string for username.
   * @param page Index of the page to retrieve (0-indexed).
   * @param size Number of items per page.
   * @param sort Sorting criteria, e.g., 'username,asc'.
   * @returns An Observable of a Page of UserResponseDto.
   */
  searchUsers(searchTerm: string = '', page: number = 0, size: number = 10, sort: string = 'username,asc'): Observable<Page<UserResponseDto>> {
    const params = {
      search: searchTerm,
      page: page.toString(),
      size: size.toString(),
      sort: sort
    };
    return this.api.get<Page<UserResponseDto>>(this.baseUrl, params);
  }

  /**
   * Updates a user's system role.
   * @param userId The ID of the user to update.
   * @param newRoleName The new system role name.
   * @returns An Observable of the updated UserResponseDto.
   */
  updateUserSystemRole(userId: number, newRoleName: string): Observable<UserResponseDto> {
    const requestBody: UpdateUserSystemRoleRequestDto = { newSystemRoleName: newRoleName };
    return this.api.put<UserResponseDto>(`${this.baseUrl}/${userId}/role`, requestBody);
  }

 
}