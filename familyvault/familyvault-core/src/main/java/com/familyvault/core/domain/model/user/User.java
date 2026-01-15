package com.familyvault.core.domain.model.user;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * User domain entity representing a registered user.
 * This is the domain model, separate from JPA entity.
 */
@Getter
@Builder(toBuilder = true)
public class User {

    private final UserId id;
    private final Email email;
    private final String passwordHash;
    private final String firstName;
    private final String lastName;
    private final String profilePicture;

    private final boolean emailVerified;
    private final AccountStatus accountStatus;

    private final int failedLoginAttempts;
    private final Instant lockedUntil;
    private final Instant lastLoginAt;

    private final Instant createdAt;
    private final Instant updatedAt;

    public enum AccountStatus {
        ACTIVE, SUSPENDED, DELETED
    }

    /**
     * Creates a new user for registration.
     */
    public static User create(Email email, String passwordHash, String firstName, String lastName) {
        return User.builder()
                .id(UserId.generate())
                .email(email)
                .passwordHash(passwordHash)
                .firstName(firstName)
                .lastName(lastName)
                .emailVerified(false)
                .accountStatus(AccountStatus.ACTIVE)
                .failedLoginAttempts(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isLocked() {
        return lockedUntil != null && Instant.now().isBefore(lockedUntil);
    }

    public boolean canLogin() {
        return accountStatus == AccountStatus.ACTIVE && !isLocked();
    }

    public User markEmailVerified() {
        return this.toBuilder()
                .emailVerified(true)
                .updatedAt(Instant.now())
                .build();
    }

    public User recordSuccessfulLogin() {
        return this.toBuilder()
                .failedLoginAttempts(0)
                .lockedUntil(null)
                .lastLoginAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public User recordFailedLogin(int maxAttempts, int lockoutMinutes) {
        int newAttempts = failedLoginAttempts + 1;
        Instant newLockedUntil = null;

        if (newAttempts >= maxAttempts) {
            newLockedUntil = Instant.now().plusSeconds(lockoutMinutes * 60L);
        }

        return this.toBuilder()
                .failedLoginAttempts(newAttempts)
                .lockedUntil(newLockedUntil)
                .updatedAt(Instant.now())
                .build();
    }

    public User updateProfile(String firstName, String lastName, String profilePicture) {
        return this.toBuilder()
                .firstName(firstName != null ? firstName : this.firstName)
                .lastName(lastName != null ? lastName : this.lastName)
                .profilePicture(profilePicture != null ? profilePicture : this.profilePicture)
                .updatedAt(Instant.now())
                .build();
    }

    public User updatePassword(String newPasswordHash) {
        return this.toBuilder()
                .passwordHash(newPasswordHash)
                .updatedAt(Instant.now())
                .build();
    }
}
