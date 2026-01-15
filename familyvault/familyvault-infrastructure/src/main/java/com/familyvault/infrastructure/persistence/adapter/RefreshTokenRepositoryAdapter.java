package com.familyvault.infrastructure.persistence.adapter;

import com.familyvault.core.application.port.out.persistence.RefreshTokenRepository;
import com.familyvault.core.domain.model.user.UserId;
import com.familyvault.infrastructure.persistence.entity.RefreshTokenEntity;
import com.familyvault.infrastructure.persistence.repository.JpaRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final JpaRefreshTokenRepository jpaRepository;

    @Override
    @Transactional
    public void save(UUID id, UserId userId, String tokenHash, String deviceInfo, String ipAddress, Instant expiresAt) {
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .id(id)
                .userId(userId.value())
                .tokenHash(tokenHash)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .expiresAt(expiresAt)
                .build();

        jpaRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshTokenData> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash)
                .map(entity -> new RefreshTokenData(
                        entity.getId(),
                        UserId.of(entity.getUserId()),
                        entity.getExpiresAt(),
                        entity.isRevoked()
                ));
    }

    @Override
    @Transactional
    public void revoke(String tokenHash) {
        jpaRepository.revokeByTokenHash(tokenHash, Instant.now());
    }

    @Override
    @Transactional
    public void revokeAllForUser(UserId userId) {
        jpaRepository.revokeAllByUserId(userId.value(), Instant.now());
    }

    @Override
    @Transactional
    public void deleteExpired() {
        jpaRepository.deleteExpired(Instant.now());
    }
}
