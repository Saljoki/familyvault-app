package com.familyvault.infrastructure.persistence.repository;

import com.familyvault.infrastructure.persistence.entity.FamilyMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaFamilyMemberRepository extends JpaRepository<FamilyMemberEntity, String> {
    Optional<FamilyMemberEntity> findByFamilyIdAndUserId(String familyId, String userId);

    List<FamilyMemberEntity> findByFamilyId(String familyId);

    List<FamilyMemberEntity> findByUserId(String userId);

    boolean existsByFamilyIdAndUserId(String familyId, String userId);
}
