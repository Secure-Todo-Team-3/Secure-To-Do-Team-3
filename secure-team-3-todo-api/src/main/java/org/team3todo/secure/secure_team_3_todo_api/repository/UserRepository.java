package org.team3todo.secure.secure_team_3_todo_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.team3todo.secure.secure_team_3_todo_api.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserGuid(UUID userGuid);
    Optional<User> findUserByEmail(String email);
    Optional<User> findByUsername(String username);
}
