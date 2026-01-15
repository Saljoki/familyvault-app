package com.familyvault.infrastructure.persistence.adapter;

import com.familyvault.core.application.port.out.persistence.FamilyRepository;
import com.familyvault.core.domain.model.family.Family;
import com.familyvault.core.domain.model.family.FamilyId;
import com.familyvault.core.domain.model.family.FamilyMember;
import com.familyvault.core.domain.model.user.UserId;
import com.familyvault.infrastructure.persistence.entity.FamilyEntity;
import com.familyvault.infrastructure.persistence.entity.FamilyMemberEntity;
import com.familyvault.infrastructure.persistence.mapper.FamilyMapper;
import com.familyvault.infrastructure.persistence.repository.JpaFamilyMemberRepository;
import com.familyvault.infrastructure.persistence.repository.JpaFamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FamilyRepositoryAdapter implements FamilyRepository {

    private final JpaFamilyRepository jpaFamilyRepository;
    private final JpaFamilyMemberRepository jpaFamilyMemberRepository;
    private final FamilyMapper familyMapper;

    @Override
    @Transactional
    public Family save(Family family) {
        FamilyEntity entity = familyMapper.toEntity(family);
        FamilyEntity saved = jpaFamilyRepository.save(entity);
        return familyMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Family> findById(FamilyId id) {
        return jpaFamilyRepository.findById(id.getValue())
                .map(familyMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Family> findByInviteCode(String inviteCode) {
        return jpaFamilyRepository.findByInviteCode(inviteCode)
                .map(familyMapper::toDomain);
    }

    @Override
    @Transactional
    public void delete(FamilyId id) {
        jpaFamilyRepository.deleteById(id.getValue());
    }

    @Override
    @Transactional
    public FamilyMember saveMember(FamilyMember member) {
        FamilyMemberEntity entity = familyMapper.toEntity(member);
        FamilyMemberEntity saved = jpaFamilyMemberRepository.save(entity);
        return familyMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FamilyMember> findMember(FamilyId familyId, UserId userId) {
        return jpaFamilyMemberRepository.findByFamilyIdAndUserId(
                        familyId.getValue(),
                        userId.getValue())
                .map(familyMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FamilyMember> findMembersByFamily(FamilyId familyId) {
        return jpaFamilyMemberRepository.findByFamilyId(familyId.getValue())
                .stream()
                .map(familyMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Family> findFamiliesByUser(UserId userId) {
        List<FamilyMemberEntity> memberEntities = jpaFamilyMemberRepository.findByUserId(userId.getValue());
        List<String> familyIds = memberEntities.stream()
                .map(FamilyMemberEntity::getFamilyId)
                .collect(Collectors.toList());

        return jpaFamilyRepository.findAllById(familyIds)
                .stream()
                .map(familyMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMember(FamilyId familyId, UserId userId) {
        return jpaFamilyMemberRepository.existsByFamilyIdAndUserId(
                familyId.getValue(),
                userId.getValue());
    }

    @Override
    @Transactional
    public void deleteMember(FamilyId familyId, UserId userId) {
        jpaFamilyMemberRepository.findByFamilyIdAndUserId(
                        familyId.getValue(),
                        userId.getValue())
                .ifPresent(jpaFamilyMemberRepository::delete);
    }
}
