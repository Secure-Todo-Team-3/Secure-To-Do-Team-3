package org.team3todo.secure.secure_team_3_todo_api.controller; // Ensure this package is scanned

import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.team3todo.secure.secure_team_3_todo_api.dto.AuthenticatedResponse;
import org.team3todo.secure.secure_team_3_todo_api.dto.AuthenticationRequest;
import org.team3todo.secure.secure_team_3_todo_api.dto.RegisterRequest;
import org.team3todo.secure.secure_team_3_todo_api.service.AuthService;

import io.jsonwebtoken.security.InvalidKeyException; 
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor 
@CrossOrigin("*")
public class AuthController { 

    private final AuthService service; 

    @PostMapping("/login")
    public ResponseEntity<AuthenticatedResponse> login(@RequestBody AuthenticationRequest authenticationReq) {
        try {
            AuthenticatedResponse response = service.login(authenticationReq);
            return ResponseEntity.ok(response);
        } catch (InvalidKeyException | IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticatedResponse> register(@RequestBody RegisterRequest registerRequest) {
        try {
            return ResponseEntity.ok(service.register(registerRequest));
        } catch (InvalidKeyException | IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
