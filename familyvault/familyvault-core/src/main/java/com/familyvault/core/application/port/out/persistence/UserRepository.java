package com.familyvault.core.application.port.out.persistence;

import com.familyvault.core.domain.model.user.Email;
import com.familyvault.core.domain.model.user.User;
import com.familyvault.core.domain.model.user.UserId;

import java.util.Optional;

/**
 * Port for user persistence operations.
 * Implemented by infrastructure layer.
 */
public interface UserRepository {

    /**
     * Save a user (create or update).
     */
    User save(User user);

    /**
     * Find user by ID.
     */
    Optional<User> findById(UserId id);

    /**
     * Find user by email.
     */
    Optional<User> findByEmail(Email email);

    /**
     * Check if email is already registered.
     */
    boolean existsByEmail(Email email);

    /**
     * Delete user (soft delete).
     */
    void delete(UserId id);
}
