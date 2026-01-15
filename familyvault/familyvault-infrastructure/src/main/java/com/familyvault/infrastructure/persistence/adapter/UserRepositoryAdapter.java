package com.familyvault.infrastructure.persistence.adapter;

import com.familyvault.core.application.port.out.persistence.UserRepository;
import com.familyvault.core.domain.model.user.Email;
import com.familyvault.core.domain.model.user.User;
import com.familyvault.core.domain.model.user.UserId;
import com.familyvault.infrastructure.persistence.entity.UserEntity;
import com.familyvault.infrastructure.persistence.mapper.UserMapper;
import com.familyvault.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaRepository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        entity = jpaRepository.save(entity);
        return mapper.toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value())
                .filter(e -> e.getDeletedAt() == null)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmailAndDeletedAtIsNull(email.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmailAndDeletedAtIsNull(email.value());
    }

    @Override
    @Transactional
    public void delete(UserId id) {
        jpaRepository.findById(id.value()).ifPresent(entity -> {
            entity.setDeletedAt(Instant.now());
            entity.setAccountStatus(UserEntity.AccountStatus.DELETED);
            jpaRepository.save(entity);
        });
    }
}
