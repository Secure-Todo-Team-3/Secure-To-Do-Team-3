package org.team3todo.secure.secure_team_3_todo_api.util; // Correct your package name if needed

import org.team3todo.secure.secure_team_3_todo_api.entity.User;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);
    private static final String AUDIT_USER_ID_CONFIG = "audit.current_user_id";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * This method runs BEFORE any method annotated with @Transactional.
     * It sets a transaction-local variable in PostgreSQL with the current user's ID.
     */
    @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void setAuditUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
                User principal = (User) authentication.getPrincipal();
                Long userId = principal.getId();

                String sql = String.format("SELECT set_config('%s', ?, true)", AUDIT_USER_ID_CONFIG);

                // This use of .query() is correct as it handles a ResultSet
                jdbcTemplate.query(sql, ps -> ps.setString(1, String.valueOf(userId)), rs -> {});
                logger.debug("Set {} to: {}", AUDIT_USER_ID_CONFIG, userId);
            }
        } catch (Exception e) {
            logger.error("Could not set audit user ID in transaction", e);
        }
    }

    /**
     * This method runs AFTER any method annotated with @Transactional.
     * It clears the transaction-local variable.
     */
    @After("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void clearAuditUser() {
        try {
            String sql = String.format("SELECT set_config('%s', '', true)", AUDIT_USER_ID_CONFIG);

            // CORRECTED: Use .query() instead of .update() to handle the ResultSet
            jdbcTemplate.query(sql, rs -> {});

            logger.debug("Cleared {}", AUDIT_USER_ID_CONFIG);
        } catch (Exception e) {
            logger.error("Could not clear audit user ID in transaction", e);
        }
    }
}