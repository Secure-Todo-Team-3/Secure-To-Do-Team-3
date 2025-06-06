package org.team3todo.secure.secure_team_3_todo_api.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.team3todo.secure.secure_team_3_todo_api.dto.AuthenticatedResponse;
import org.team3todo.secure.secure_team_3_todo_api.dto.AuthenticationRequest;
import org.team3todo.secure.secure_team_3_todo_api.dto.RegisterRequest;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.repository.UserRepository;
import org.team3todo.secure.secure_team_3_todo_api.util.CustomPasswordEncoder;

import io.jsonwebtoken.security.InvalidKeyException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CustomPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticatedResponse register(RegisterRequest registerRequest) throws InvalidKeyException, IOException {
        var user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .totpEnabled(false)
                .build();
        var userCreated = userRepository.save(user);
        var token = jwtService.generateToken(Map.of("userGuid",userCreated.getUserGuid()), user);
        return AuthenticatedResponse.builder().token(token).build();
    }
    public AuthenticatedResponse login(AuthenticationRequest authenticationReq) throws InvalidKeyException, IOException {
       Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authenticationReq.getUsername(), authenticationReq.getPassword())
        );
        if(!authentication.isAuthenticated()) return null;
        var user = userRepository.findByUsername(authenticationReq.getUsername()).orElseThrow();
        var token = jwtService.generateToken(Map.of("userGuid",user.getUserGuid()), user);
        return AuthenticatedResponse.builder().token(token).build();
    }
}
