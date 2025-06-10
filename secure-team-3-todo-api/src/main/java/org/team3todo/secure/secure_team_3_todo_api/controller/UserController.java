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
    UUID userGuid = (UUID) authentication.getPrincipal();
    User user = userService.findByUserGuid(userGuid);
    return ResponseEntity.ok(userMapper.convertToDto(user));
}
}
