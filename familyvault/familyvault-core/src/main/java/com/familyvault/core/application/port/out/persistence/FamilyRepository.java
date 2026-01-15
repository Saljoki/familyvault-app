package com.familyvault.core.application.port.out.persistence;

import com.familyvault.core.domain.model.family.Family;
import com.familyvault.core.domain.model.family.FamilyId;
import com.familyvault.core.domain.model.family.FamilyMember;
import com.familyvault.core.domain.model.user.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Port for family persistence operations.
 */
public interface FamilyRepository {

    // Family operations
    Family save(Family family);

    Optional<Family> findById(FamilyId id);

    Optional<Family> findByInviteCode(String inviteCode);

    void delete(FamilyId id);

    // Member operations
    FamilyMember saveMember(FamilyMember member);

    Optional<FamilyMember> findMember(FamilyId familyId, UserId userId);

    List<FamilyMember> findMembersByFamily(FamilyId familyId);

    List<FamilyMember> findFamiliesByUser(UserId userId);

    boolean isMember(FamilyId familyId, UserId userId);

    void deleteMember(FamilyId familyId, UserId userId);
}
