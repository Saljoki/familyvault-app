package com.familyvault.core.application.dto.response;

import com.familyvault.core.domain.model.user.User;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String profilePicture,
        boolean emailVerified,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId().value(),
                user.getEmail().value(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfilePicture(),
                user.isEmailVerified(),
                user.getCreatedAt()
        );
    }
}
