package com.familyvault.core.application.port.out.persistence;

import com.familyvault.core.domain.model.user.UserId;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Port for refresh token persistence.
 */
public interface RefreshTokenRepository {

    /**
     * Save a new refresh token.
     */
    void save(UUID id, UserId userId, String tokenHash, String deviceInfo, String ipAddress, Instant expiresAt);

    /**
     * Find a token by its hash.
     */
    Optional<RefreshTokenData> findByTokenHash(String tokenHash);

    /**
     * Revoke a token (mark as revoked).
     */
    void revoke(String tokenHash);

    /**
     * Revoke all tokens for a user.
     */
    void revokeAllForUser(UserId userId);

    /**
     * Delete expired tokens (cleanup job).
     */
    void deleteExpired();

    /**
     * Data holder for refresh token info.
     */
    record RefreshTokenData(
            UUID id,
            UserId userId,
            Instant expiresAt,
            boolean revoked
    ) {
        public boolean isValid() {
            return !revoked && Instant.now().isBefore(expiresAt);
        }
    }
}
