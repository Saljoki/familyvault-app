package com.familyvault.core.application.service.auth;

import com.familyvault.core.application.dto.request.LoginRequest;
import com.familyvault.core.application.dto.request.RefreshTokenRequest;
import com.familyvault.core.application.dto.request.RegisterRequest;
import com.familyvault.core.application.dto.response.AuthResponse;
import com.familyvault.core.application.dto.response.UserResponse;
import com.familyvault.core.application.exception.*;
import com.familyvault.core.application.port.out.persistence.UserRepository;
import com.familyvault.core.application.port.out.security.PasswordEncoderPort;
import com.familyvault.core.application.port.out.security.TokenPort;
import com.familyvault.core.domain.model.user.Email;
import com.familyvault.core.domain.model.user.User;
import com.familyvault.core.domain.model.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Application service for authentication operations.
 */
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenPort tokenPort;

    /**
     * Register a new user.
     */
    public AuthResponse register(RegisterRequest request) {
        Email email = Email.of(request.email());

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(request.email());
        }

        // Create user
        String passwordHash = passwordEncoder.encode(request.password());
        User user = User.create(email, passwordHash, request.firstName(), request.lastName());

        user = userRepository.save(user);
        log.info("User registered: {}", user.getId());

        // Generate tokens
        return generateAuthResponse(user);
    }

    /**
     * Login with email and password.
     */
    public AuthResponse login(LoginRequest request) {
        Email email = Email.of(request.email());

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        // Check if account is locked
        if (user.isLocked()) {
            throw new AccountLockedException(user.getLockedUntil());
        }

        // Check if account is active
        if (user.getAccountStatus() != User.AccountStatus.ACTIVE) {
            throw new InvalidCredentialsException();
        }

        // Verify password
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            // Record failed attempt
            user = user.recordFailedLogin(MAX_LOGIN_ATTEMPTS, LOCKOUT_MINUTES);
            userRepository.save(user);

            if (user.isLocked()) {
                log.warn("Account locked after {} failed attempts: {}", MAX_LOGIN_ATTEMPTS, email);
                throw new AccountLockedException(user.getLockedUntil());
            }

            throw new InvalidCredentialsException();
        }

        // Successful login
        user = user.recordSuccessfulLogin();
        user = userRepository.save(user);
        log.info("User logged in: {}", user.getId());

        return generateAuthResponse(user);
    }

    /**
     * Refresh access token using refresh token.
     */
    public AuthResponse refresh(RefreshTokenRequest request) {
        UserId userId = tokenPort.validateRefreshToken(request.refreshToken())
                .orElseThrow(InvalidTokenException::new);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getAccountStatus() != User.AccountStatus.ACTIVE) {
            throw new InvalidTokenException("Account is not active");
        }

        // Revoke old refresh token and generate new ones
        tokenPort.revokeRefreshToken(request.refreshToken());

        log.debug("Token refreshed for user: {}", userId);
        return generateAuthResponse(user);
    }

    /**
     * Logout - revoke refresh token.
     */
    public void logout(String refreshToken) {
        tokenPort.revokeRefreshToken(refreshToken);
        log.debug("User logged out");
    }

    /**
     * Logout from all devices.
     */
    public void logoutAll(UserId userId) {
        tokenPort.revokeAllUserTokens(userId);
        log.info("User logged out from all devices: {}", userId);
    }

    /**
     * Get current user info.
     */
    public UserResponse getCurrentUser(UserId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return UserResponse.from(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        String accessToken = tokenPort.generateAccessToken(user.getId(), user.getEmail().value());
        String refreshToken = tokenPort.generateRefreshToken(user.getId());
        long expiresIn = tokenPort.getAccessTokenExpirationSeconds();

        return AuthResponse.of(accessToken, refreshToken, expiresIn, UserResponse.from(user));
    }
}
