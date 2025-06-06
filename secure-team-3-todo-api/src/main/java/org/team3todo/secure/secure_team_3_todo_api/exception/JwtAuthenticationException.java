package org.team3todo.secure.secure_team_3_todo_api.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Custom exception for handling errors related to JWT processing,
 * such as parsing, validation, and signature errors.
 * Extends AuthenticationException to integrate with Spring Sec appropriately.
 */
public class JwtAuthenticationException extends AuthenticationException {

    public JwtAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JwtAuthenticationException(String msg) {
        super(msg);
    }
}
