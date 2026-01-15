package com.familyvault.core.application.port.out.security;

/**
 * Port for password encoding operations.
 */
public interface PasswordEncoderPort {

    /**
     * Encode a raw password.
     */
    String encode(String rawPassword);

    /**
     * Check if a raw password matches an encoded password.
     */
    boolean matches(String rawPassword, String encodedPassword);
}
