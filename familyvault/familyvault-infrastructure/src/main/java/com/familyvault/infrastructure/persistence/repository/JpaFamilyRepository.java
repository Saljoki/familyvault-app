package com.familyvault.infrastructure.persistence.repository;

import com.familyvault.infrastructure.persistence.entity.FamilyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaFamilyRepository extends JpaRepository<FamilyEntity, String> {
    Optional<FamilyEntity> findByInviteCode(String inviteCode);
}
