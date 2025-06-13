
package org.team3todo.secure.secure_team_3_todo_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.team3todo.secure.secure_team_3_todo_api.dto.UpdateUserSystemRoleRequestDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.UserResponseDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.SystemRole;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.repository.SystemRoleRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors; 

@Service
public class AdminUserManagementService {

    private final UserRepository userRepository;
    private final SystemRoleRepository systemRoleRepository;

    public AdminUserManagementService(UserRepository userRepository, SystemRoleRepository systemRoleRepository) {
        this.userRepository = userRepository;
        this.systemRoleRepository = systemRoleRepository;
    }

    /**
     * Searches for users across the system by username or email, paginated.
     * Adapts to UserRepository's limitations by performing in-memory pagination for searches.
     *
     * @param searchTerm Optional string to search within username.
     * @param pageable Pagination and sorting information.
     * @return A Page of UserResponseDto.
     */
    @Transactional(readOnly = true)
    public Page<UserResponseDto> searchUsers(String searchTerm, Pageable pageable) {
        List<User> allUsersMatchingSearch;

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // CRITICAL CHANGE: Call the findByUsernameContainingIgnoreCase that does NOT take Pageable
            // This method is expected to return a List<User> (all matching users).
            allUsersMatchingSearch = userRepository.findByUsernameContainingIgnoreCase(searchTerm);
        } else {
            // If no search term, use findAll with Pageable for efficient database-level pagination
            Page<User> usersPage = userRepository.findAll(pageable);
            return usersPage.map(this::convertToUserResponseDto);
        }

        // Now, manually paginate the 'allUsersMatchingSearch' list in memory
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allUsersMatchingSearch.size());

        List<User> pageContent = allUsersMatchingSearch.subList(start, end);
        Page<User> paginatedUsers = new PageImpl<>(pageContent, pageable, allUsersMatchingSearch.size());

        return paginatedUsers.map(this::convertToUserResponseDto);
    }

    /**
     * Grants (or changes) a system-wide role to a specific user.
     *
     * @param userId The ID of the user to update.
     * @param requestDto DTO containing the new system role name.
     * @return The updated UserResponseDto.
     * @throws IllegalArgumentException if the user or the new role is not found.
     */
    @Transactional
    public UserResponseDto grantUserSystemRole(Long userId, UpdateUserSystemRoleRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        String newRoleName = requestDto.getNewSystemRoleName().toUpperCase();
        SystemRole newSystemRole = systemRoleRepository.findByName(newRoleName)
                .orElseThrow(() -> new IllegalArgumentException("System role '" + newRoleName + "' not found."));

        user.setSystemRole(newSystemRole);
        User updatedUser = userRepository.save(user);

        return convertToUserResponseDto(updatedUser);
    }

    private UserResponseDto convertToUserResponseDto(User user) {
        String systemRoleName = (user.getSystemRole() != null) ? user.getSystemRole().getName() : null;
        return UserResponseDto.builder()
                .id(user.getId())
                .userGuid(user.getUserGuid())
                .username(user.getUsername())
                .email(user.getEmail())
                .systemRole(systemRoleName)
                .isLocked(user.getIsLocked())
                .isActive(user.getIsActive())
                .build();
    }
}