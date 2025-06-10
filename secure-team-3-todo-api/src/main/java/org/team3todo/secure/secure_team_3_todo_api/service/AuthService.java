package org.team3todo.secure.secure_team_3_todo_api.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.team3todo.secure.secure_team_3_todo_api.dto.AuthenticatedResponseDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.AuthenticationRequestDto;
import org.team3todo.secure.secure_team_3_todo_api.dto.RegisterRequest;
import org.team3todo.secure.secure_team_3_todo_api.entity.SystemRole;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.exception.DuplicateResourceException;
import org.team3todo.secure.secure_team_3_todo_api.exception.ResourceNotFoundException;
import org.team3todo.secure.secure_team_3_todo_api.repository.SystemRoleRepository;
import org.team3todo.secure.secure_team_3_todo_api.repository.UserRepository;
import org.team3todo.secure.secure_team_3_todo_api.util.CustomPasswordEncoder;
import org.team3todo.secure.secure_team_3_todo_api.dto.TotpSetupResponse;
import org.team3todo.secure.secure_team_3_todo_api.dto.TotpVerificationRequest;
import io.jsonwebtoken.security.InvalidKeyException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthService {
    private final CustomPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SystemRoleRepository systemRoleRepository;
    private final TotpService totpService;

    @Transactional
    public TotpSetupResponse registerAndInitiateTotp(RegisterRequest registerRequest) throws InvalidKeyException, IOException {
        String secret = totpService.generateNewSecret();
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()){
            throw new DuplicateResourceException("Username: "+registerRequest.getUsername()+" is already taken.");
        }

        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
            throw new DuplicateResourceException("Email: "+registerRequest.getUsername()+" is already taken.");
        }

        SystemRole defaultRole = systemRoleRepository.findByName("REGULAR_USER").orElseThrow(() -> new ResourceNotFoundException("Default role REGULAR_USER not found."));

        var user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .systemRole(defaultRole)
                .isTotpEnabled(false)
                .isActive(false)
                .totpSecret(secret) 
                .build();
        
        userRepository.save(user);

        return TotpSetupResponse.builder()
                .secret(secret)
                .qrCodeImageUri(totpService.generateQrCodeImageUri(secret, user.getEmail()))
                .message("Scan QR code and enter the code to complete registration.")
                .build();
    }
    
    @Transactional
    public AuthenticatedResponseDto verifyInitialTotpAndActivateUser(TotpVerificationRequest verificationRequest) throws InvalidKeyException, IOException {
        var user = userRepository.findByUsername(verificationRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found during verification."));

        if (user.getIsActive()) {
            throw new IllegalStateException("User is already active.");
        }

        if (!totpService.isCodeValid(user.getTotpSecret(), verificationRequest.getCode())) {
            throw new SecurityException("Invalid TOTP code during registration verification.");
        }

        user.setIsActive(true);;
        user.setTotpEnabled(true);
        userRepository.save(user);

        var token = jwtService.generateToken(Map.of("userGuid", user.getUserGuid()), user);
        return AuthenticatedResponseDto.builder().token(token).build();
    }


    public AuthenticatedResponseDto login(AuthenticationRequestDto authenticationReq) throws InvalidKeyException, IOException {
       Authentication authentication = authenticationManager.authenticate(
               new UsernamePasswordAuthenticationToken(authenticationReq.getUsername(), authenticationReq.getPassword())
       );
       
       User user = userRepository.findByUsername(authenticationReq.getUsername())
               .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

       if (user.isTotpEnabled()) {
           return AuthenticatedResponseDto.builder()
                   .totpRequired(true)
                   .message("TOTP code is required")
                   .build();
       }
       
       var token = jwtService.generateToken(Map.of("userGuid",user.getUserGuid()), user);
       return AuthenticatedResponseDto.builder().token(token).totpRequired(false).build();
    }

    @Transactional
    public AuthenticatedResponseDto loginWithTotp(TotpVerificationRequest verificationRequest) throws InvalidKeyException, IOException {
        var user = userRepository.findByUsername(verificationRequest.getUsername())
                     .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!totpService.isCodeValid(user.getTotpSecret(), verificationRequest.getCode())) {
            throw new SecurityException("Invalid TOTP code.");
        }
        
        var token = jwtService.generateToken(Map.of("userGuid", user.getUserGuid()), user);
        return AuthenticatedResponseDto.builder().token(token).totpRequired(false).build();
    }
    
    @Transactional
    public TotpSetupResponse setupTotp(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        if (user.isTotpEnabled()) {
            throw new IllegalStateException("TOTP is already enabled for this user.");
        }

        String secret = totpService.generateNewSecret();
        String qrCodeUri = totpService.generateQrCodeImageUri(secret, user.getEmail());

        user.setTotpSecret(secret);
        userRepository.save(user);

        return TotpSetupResponse.builder()
                .secret(secret) 
                .qrCodeImageUri(qrCodeUri) 
                .message("Scan the QR code and verify to enable TOTP.")
                .build();
    }
    
    @Transactional
    public void verifyAndEnableTotp(Authentication authentication, TotpVerificationRequest verificationRequest) {
        User user = (User) authentication.getPrincipal();
        
        if (!totpService.isCodeValid(user.getTotpSecret(), verificationRequest.getCode())) {
             throw new SecurityException("Invalid verification code.");
        }

        user.setTotpEnabled(true);
        userRepository.save(user);
    }
}