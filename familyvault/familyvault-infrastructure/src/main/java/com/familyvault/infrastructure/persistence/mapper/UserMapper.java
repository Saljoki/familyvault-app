package com.familyvault.infrastructure.persistence.mapper;

import com.familyvault.core.domain.model.user.Email;
import com.familyvault.core.domain.model.user.User;
import com.familyvault.core.domain.model.user.UserId;
import com.familyvault.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.builder()
                .id(UserId.of(entity.getId()))
                .email(Email.of(entity.getEmail()))
                .passwordHash(entity.getPasswordHash())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .profilePicture(entity.getProfilePicture())
                .emailVerified(entity.isEmailVerified())
                .accountStatus(mapAccountStatus(entity.getAccountStatus()))
                .failedLoginAttempts(entity.getFailedLoginAttempts())
                .lockedUntil(entity.getLockedUntil())
                .lastLoginAt(entity.getLastLoginAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        return UserEntity.builder()
                .id(domain.getId().value())
                .email(domain.getEmail().value())
                .passwordHash(domain.getPasswordHash())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .profilePicture(domain.getProfilePicture())
                .emailVerified(domain.isEmailVerified())
                .accountStatus(mapAccountStatus(domain.getAccountStatus()))
                .failedLoginAttempts(domain.getFailedLoginAttempts())
                .lockedUntil(domain.getLockedUntil())
                .lastLoginAt(domain.getLastLoginAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private User.AccountStatus mapAccountStatus(UserEntity.AccountStatus status) {
        if (status == null) return User.AccountStatus.ACTIVE;
        return switch (status) {
            case ACTIVE -> User.AccountStatus.ACTIVE;
            case SUSPENDED -> User.AccountStatus.SUSPENDED;
            case DELETED -> User.AccountStatus.DELETED;
        };
    }

    private UserEntity.AccountStatus mapAccountStatus(User.AccountStatus status) {
        if (status == null) return UserEntity.AccountStatus.ACTIVE;
        return switch (status) {
            case ACTIVE -> UserEntity.AccountStatus.ACTIVE;
            case SUSPENDED -> UserEntity.AccountStatus.SUSPENDED;
            case DELETED -> UserEntity.AccountStatus.DELETED;
        };
    }
}
