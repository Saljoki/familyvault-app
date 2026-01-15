package com.familyvault.core.application.port.out.security;

import com.familyvault.core.domain.model.user.UserId;

import java.util.Optional;

/**
 * Port for JWT token operations.
 */
public interface TokenPort {

    /**
     * Generate an access token for a user.
     */
    String generateAccessToken(UserId userId, String email);

    /**
     * Generate a refresh token for a user.
     */
    String generateRefreshToken(UserId userId);

    /**
     * Validate an access token and extract the user ID.
     */
    Optional<UserId> validateAccessToken(String token);

    /**
     * Validate a refresh token and extract the user ID.
     */
    Optional<UserId> validateRefreshToken(String token);

    /**
     * Get access token expiration in seconds.
     */
    long getAccessTokenExpirationSeconds();

    /**
     * Revoke a refresh token.
     */
    void revokeRefreshToken(String token);

    /**
     * Revoke all refresh tokens for a user.
     */
    void revokeAllUserTokens(UserId userId);
}
