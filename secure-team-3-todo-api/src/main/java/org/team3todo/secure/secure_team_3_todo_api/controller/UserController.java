package org.team3todo.secure.secure_team_3_todo_api.controller;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.UserDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.mapper.UserMapper;
import org.team3todo.secure.secure_team_3_todo_api.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

     @GetMapping("/{userGuid}")
    public ResponseEntity<UserDto> getUserByGUID(@PathVariable UUID userGuid){
        User foundUser = userService.findByUserGuid(userGuid);
        UserDto userDto = userMapper.convertToDto(foundUser);
        if(foundUser != null){
            return ResponseEntity.ok(userDto);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
@GetMapping("/me")
public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
    // 1. The principal is now a UUID object, so we cast to UUID.
    UUID userGuid = (UUID) authentication.getPrincipal();

    // 2. Use the GUID to find the user in the database via your service.
    //    (This assumes you have a findByGuid method in your UserService)
    User user = userService.findByUserGuid(userGuid);
       //     .orElseThrow(() -> new RuntimeException("Authenticated user with GUID '" + userGuid + "' not found in database."));

    // 3. Convert the User entity to a DTO and return it.
    return ResponseEntity.ok(userMapper.convertToDto(user));
}
}
