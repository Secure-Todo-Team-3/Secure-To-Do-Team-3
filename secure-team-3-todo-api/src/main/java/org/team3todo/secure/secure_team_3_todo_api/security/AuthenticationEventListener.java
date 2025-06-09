package org.team3todo.secure.secure_team_3_todo_api.security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.team3todo.secure.secure_team_3_todo_api.service.UserService;

@Component
public class AuthenticationEventListener {

    private final UserService userService;

    public AuthenticationEventListener(UserService userService) {
        this.userService = userService;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        userService.resetLoginAttempts(username);
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        userService.handleFailedLoginAttempt(username);
    }
}