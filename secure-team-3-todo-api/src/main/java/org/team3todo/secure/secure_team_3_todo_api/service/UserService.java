package org.team3todo.secure.secure_team_3_todo_api.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.team3todo.secure.secure_team_3_todo_api.exception.ResourceNotFoundException;
import org.team3todo.secure.secure_team_3_todo_api.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService{
    private final UserRepository userRepository;
    private final AuditingService auditingService;

    @Value("${security.login.max-attempts}")
    private int maxLoginAttempts;

    @Autowired
    public UserService(UserRepository userRepository, AuditingService auditingService) {
        this.userRepository = userRepository;
        this.auditingService = auditingService;
    }

    public User findByUserGuid(UUID guid) {
        return userRepository.findByUserGuid(guid).orElseThrow(() -> new ResourceNotFoundException("User does not exist."));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found"));
    }

    public User findByUserId(Long id){
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public User findByUserEmail(String email){
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

    public void resetLoginAttempts(String username){
        UUID currentUserGuid = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = findByUserGuid(currentUserGuid);
        auditingService.setAuditUser(currentUser);

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("This user does not exist. This should not be happening."));
        user.setLoginAttempts(0);
        userRepository.save(user);
    }
     public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void handleFailedLoginAttempt(String username){
        UUID currentUserGuid = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = findByUserGuid(currentUserGuid);
        auditingService.setAuditUser(currentUser);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("This user does not exist. This should not be happening."));
        int updatedLoginAttempts = user.getLoginAttempts() +1;
        user.setLoginAttempts(updatedLoginAttempts);
        if (updatedLoginAttempts >= maxLoginAttempts){
            user.setIsLocked(true);
        }
        userRepository.save(user);
    }

}
