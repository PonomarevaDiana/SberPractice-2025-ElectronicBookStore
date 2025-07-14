package com.example.demo.repository;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    List<User> findByRole(User.Role role);
    // Упрощенный метод поиска по роли и строке
    @Query("SELECT u FROM User u WHERE u.role = :role " +
            "AND (u.username LIKE %:search% OR u.email LIKE %:search%)")
    List<User> findByRoleAndSearch(@Param("role") User.Role role,
                                   @Param("search") String search);

    Optional<User> findByUsernameOrEmail(String usernameOrEmail, String usernameOrEmail1);
}

