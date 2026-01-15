package com.familyvault.core.domain.model.family;

import com.familyvault.core.domain.model.user.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a user's membership in a family.
 */
@Getter
@Builder(toBuilder = true)
public class FamilyMember {

    private final UUID id;
    private final FamilyId familyId;
    private final UserId userId;
    private final FamilyRole role;
    private final MemberStatus status;
    private final UserId invitedBy;
    private final Instant joinedAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    public enum MemberStatus {
        PENDING,
        ACTIVE,
        LEFT
    }

    /**
     * Creates a new family member (owner).
     */
    public static FamilyMember createOwner(FamilyId familyId, UserId userId) {
        Instant now = Instant.now();
        return FamilyMember.builder()
                .id(UUID.randomUUID())
                .familyId(familyId)
                .userId(userId)
                .role(FamilyRole.OWNER)
                .status(MemberStatus.ACTIVE)
                .joinedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Creates a new invited member (pending).
     */
    public static FamilyMember createInvited(FamilyId familyId, UserId userId, UserId invitedBy) {
        Instant now = Instant.now();
        return FamilyMember.builder()
                .id(UUID.randomUUID())
                .familyId(familyId)
                .userId(userId)
                .role(FamilyRole.MEMBER)
                .status(MemberStatus.PENDING)
                .invitedBy(invitedBy)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Creates an active member (joined via invite code).
     */
    public static FamilyMember createActive(FamilyId familyId, UserId userId) {
        Instant now = Instant.now();
        return FamilyMember.builder()
                .id(UUID.randomUUID())
                .familyId(familyId)
                .userId(userId)
                .role(FamilyRole.MEMBER)
                .status(MemberStatus.ACTIVE)
                .joinedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public FamilyMember activate() {
        return this.toBuilder()
                .status(MemberStatus.ACTIVE)
                .joinedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public FamilyMember leave() {
        return this.toBuilder()
                .status(MemberStatus.LEFT)
                .updatedAt(Instant.now())
                .build();
    }

    public FamilyMember changeRole(FamilyRole newRole) {
        return this.toBuilder()
                .role(newRole)
                .updatedAt(Instant.now())
                .build();
    }

    public boolean isActive() {
        return status == MemberStatus.ACTIVE;
    }

    public boolean isOwner() {
        return role == FamilyRole.OWNER;
    }

    public boolean isAdmin() {
        return role == FamilyRole.ADMIN || role == FamilyRole.OWNER;
    }

    public boolean canManageMembers() {
        return isAdmin();
    }

    public boolean canUploadFiles() {
        return isActive();
    }

    public boolean canDeleteFiles() {
        return isAdmin();
    }
}
