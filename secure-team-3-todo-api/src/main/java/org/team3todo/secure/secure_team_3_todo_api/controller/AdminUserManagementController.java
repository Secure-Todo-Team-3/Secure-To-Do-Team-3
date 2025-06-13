package org.team3todo.secure.secure_team_3_todo_api.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.team3todo.secure.secure_team_3_todo_api.dto.UpdateUserSystemRoleRequestDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.UserResponseDto;
import org.team3todo.secure.secure_team_3_todo_api.service.AdminUserManagementService;

@RestController
@RequestMapping("/api/admin/management") 
public class AdminUserManagementController {

    private final AdminUserManagementService adminUserManagementService;

    public AdminUserManagementController(AdminUserManagementService adminUserManagementService) {
        this.adminUserManagementService = adminUserManagementService;
    }

    
    @PreAuthorize("hasRole('ADMIN')") 
    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDto>> searchUsers(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<UserResponseDto> usersPage = adminUserManagementService.searchUsers(search, pageable);
        return ResponseEntity.ok(usersPage);
    }


    @PreAuthorize("hasRole('ADMIN')") 
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<UserResponseDto> grantUserSystemRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserSystemRoleRequestDto requestDto) {
        UserResponseDto updatedUser = adminUserManagementService.grantUserSystemRole(userId, requestDto);
        return ResponseEntity.ok(updatedUser);
    }
}