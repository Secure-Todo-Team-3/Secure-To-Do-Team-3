package org.team3todo.secure.secure_team_3_todo_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;

@Service
public class AuditingService {

    private static final Logger logger = LoggerFactory.getLogger(AuditingService.class);
    private static final String AUDIT_USER_ID_CONFIG = "audit.current_user_id";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Sets the 'audit.current_user_id' for the current database transaction.
     * @param user The user performing the action.
     */
    public void setAuditUser(User user) {
        if (user == null || user.getId() == null) {
            logger.warn("AuditingService: Cannot set audit user because provided user or user ID is null.");
            return;
        }
        try {
            String sql = String.format("SELECT set_config('%s', ?, true)", AUDIT_USER_ID_CONFIG);
            jdbcTemplate.query(sql, ps -> ps.setString(1, String.valueOf(user.getId())), rs -> {});
            logger.debug("AuditingService: Set audit user ID to: {}", user.getId());
        } catch (Exception e) {
            logger.error("AuditingService: FAILED to set audit user ID.", e);
        }
    }

    /**
     * Clears the 'audit.current_user_id' for the current database transaction.
     */
    public void clearAuditUser() {
        try {
            String sql = "SELECT set_config('audit.current_user_id', NULL, true)";
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            logger.error("AuditingService: FAILED to clear audit user ID.", e);
        }
    }
}