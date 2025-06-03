package org.team3todo.secure.secure_team_3_todo_api.controller;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.team3todo.secure.secure_team_3_todo_api.dto.TeamDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.UserDto;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userGuid}")
    public ResponseEntity<UserDto> getUserByGUID(@PathVariable UUID userGuid){
        UserDto foundUser = userService.findByUserGuid(userGuid);
        if(foundUser != null){
            return ResponseEntity.ok(foundUser);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
}
