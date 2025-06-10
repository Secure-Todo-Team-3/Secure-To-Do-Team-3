package org.team3todo.secure.secure_team_3_todo_api.controller;

import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.team3todo.secure.secure_team_3_todo_api.dto.AuthenticatedResponseDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.AuthenticationRequestDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.RegisterRequest;
import org.team3todo.secure.secure_team_3_todo_api.dto.TotpSetupResponse;
import org.team3todo.secure.secure_team_3_todo_api.dto.TotpVerificationRequest;
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
    public ResponseEntity<AuthenticatedResponseDto> login(@RequestBody AuthenticationRequestDto authenticationReq) {
        try {
            AuthenticatedResponseDto response = service.login(authenticationReq);
            return ResponseEntity.ok(response);
        } catch (InvalidKeyException | IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/login/verify-totp")
    public ResponseEntity<AuthenticatedResponseDto> loginWithTotp(@RequestBody TotpVerificationRequest request) throws InvalidKeyException, IOException {
        return ResponseEntity.ok(service.loginWithTotp(request));
    }

    @PostMapping("/totp/setup")
    public ResponseEntity<TotpSetupResponse> setupTotp(Authentication authentication) {
        return ResponseEntity.ok(service.setupTotp(authentication));
    }

    @PostMapping("register/totp/verify")
    public ResponseEntity<AuthenticatedResponseDto> verifyTotp(Authentication authentication, @RequestBody TotpVerificationRequest request) {

        try {
            return ResponseEntity.ok(service.verifyInitialTotpAndActivateUser(request));
        } catch (InvalidKeyException | IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/registerAndInitiateTotp")
    public ResponseEntity<TotpSetupResponse> register(@RequestBody RegisterRequest registerRequest) {
        try {
            return ResponseEntity.ok(service.registerAndInitiateTotp(registerRequest));
        } catch (InvalidKeyException | IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}