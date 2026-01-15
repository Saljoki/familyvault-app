package com.familyvault.core.application.service.family;

import com.familyvault.core.application.dto.request.CreateFamilyRequest;
import com.familyvault.core.application.dto.request.JoinFamilyRequest;
import com.familyvault.core.application.dto.response.FamilyMemberResponse;
import com.familyvault.core.application.dto.response.FamilyResponse;
import com.familyvault.core.application.exception.FamilyNotFoundException;
import com.familyvault.core.application.exception.UnauthorizedException;
import com.familyvault.core.application.exception.UserNotFoundException;
import com.familyvault.core.application.port.out.persistence.FamilyRepository;
import com.familyvault.core.application.port.out.persistence.UserRepository;
import com.familyvault.core.domain.model.family.Family;
import com.familyvault.core.domain.model.family.FamilyId;
import com.familyvault.core.domain.model.family.FamilyMember;
import com.familyvault.core.domain.model.family.FamilyRole;
import com.familyvault.core.domain.model.user.User;
import com.familyvault.core.domain.model.user.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;

    @Transactional
    public FamilyResponse createFamily(CreateFamilyRequest request, UserId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Family family = Family.create(
                request.getName(),
                request.getDescription(),
                request.getFamilyPicture(),
                userId
        );

        Family savedFamily = familyRepository.save(family);

        // Add creator as owner
        FamilyMember owner = FamilyMember.create(
                savedFamily.getId(),
                userId,
                FamilyRole.OWNER
        );
        owner.activate();
        familyRepository.saveMember(owner);

        return toFamilyResponse(savedFamily);
    }

    @Transactional
    public FamilyResponse joinFamily(JoinFamilyRequest request, UserId userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Family family = familyRepository.findByInviteCode(request.getInviteCode())
                .orElseThrow(() -> new FamilyNotFoundException("Family not found with invite code"));

        if (!family.getInviteEnabled()) {
            throw new UnauthorizedException("Family invites are disabled");
        }

        // Check if already a member
        if (familyRepository.isMember(family.getId(), userId)) {
            throw new IllegalStateException("User is already a member of this family");
        }

        FamilyMember member = FamilyMember.create(family.getId(), userId, FamilyRole.MEMBER);
        member.activate();
        familyRepository.saveMember(member);

        return toFamilyResponse(family);
    }

    @Transactional(readOnly = true)
    public List<FamilyResponse> listFamilies(UserId userId) {
        return familyRepository.findFamiliesByUser(userId)
                .stream()
                .map(this::toFamilyResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FamilyResponse getFamily(FamilyId familyId, UserId userId) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException("Family not found"));

        if (!familyRepository.isMember(familyId, userId)) {
            throw new UnauthorizedException("User is not a member of this family");
        }

        return toFamilyResponse(family);
    }

    @Transactional(readOnly = true)
    public List<FamilyMemberResponse> listMembers(FamilyId familyId, UserId userId) {
        if (!familyRepository.isMember(familyId, userId)) {
            throw new UnauthorizedException("User is not a member of this family");
        }

        List<FamilyMember> members = familyRepository.findMembersByFamily(familyId);

        return members.stream()
                .map(member -> {
                    User user = userRepository.findById(member.getUserId()).orElse(null);
                    return toMemberResponse(member, user);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public FamilyResponse regenerateInviteCode(FamilyId familyId, UserId userId) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException("Family not found"));

        FamilyMember member = familyRepository.findMember(familyId, userId)
                .orElseThrow(() -> new UnauthorizedException("User is not a member of this family"));

        if (!member.canManageMembers()) {
            throw new UnauthorizedException("User does not have permission to manage invites");
        }

        family.regenerateInviteCode();
        Family updated = familyRepository.save(family);

        return toFamilyResponse(updated);
    }

    @Transactional
    public FamilyResponse toggleInvite(FamilyId familyId, UserId userId, boolean enabled) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException("Family not found"));

        FamilyMember member = familyRepository.findMember(familyId, userId)
                .orElseThrow(() -> new UnauthorizedException("User is not a member of this family"));

        if (!member.canManageMembers()) {
            throw new UnauthorizedException("User does not have permission to manage invites");
        }

        family.setInviteEnabled(enabled);
        Family updated = familyRepository.save(family);

        return toFamilyResponse(updated);
    }

    @Transactional
    public void updateMemberRole(FamilyId familyId, UserId targetUserId, FamilyRole newRole, UserId currentUserId) {
        FamilyMember currentMember = familyRepository.findMember(familyId, currentUserId)
                .orElseThrow(() -> new UnauthorizedException("User is not a member of this family"));

        if (!currentMember.canManageMembers()) {
            throw new UnauthorizedException("User does not have permission to manage members");
        }

        FamilyMember targetMember = familyRepository.findMember(familyId, targetUserId)
                .orElseThrow(() -> new UserNotFoundException("Target user is not a member of this family"));

        targetMember.changeRole(newRole);
        familyRepository.saveMember(targetMember);
    }

    @Transactional
    public void removeMember(FamilyId familyId, UserId targetUserId, UserId currentUserId) {
        FamilyMember currentMember = familyRepository.findMember(familyId, currentUserId)
                .orElseThrow(() -> new UnauthorizedException("User is not a member of this family"));

        if (!currentMember.canManageMembers() && !currentUserId.equals(targetUserId)) {
            throw new UnauthorizedException("User does not have permission to remove members");
        }

        FamilyMember targetMember = familyRepository.findMember(familyId, targetUserId)
                .orElseThrow(() -> new UserNotFoundException("Target user is not a member of this family"));

        if (targetMember.getRole() == FamilyRole.OWNER) {
            throw new IllegalStateException("Cannot remove the family owner");
        }

        targetMember.leave();
        familyRepository.saveMember(targetMember);
    }

    private FamilyResponse toFamilyResponse(Family family) {
        return FamilyResponse.builder()
                .id(family.getId().getValue())
                .name(family.getName())
                .description(family.getDescription())
                .familyPicture(family.getFamilyPicture())
                .inviteCode(family.getInviteCode())
                .inviteEnabled(family.getInviteEnabled())
                .storageLimitBytes(family.getStorageLimitBytes())
                .storageUsedBytes(family.getStorageUsedBytes())
                .storageUsagePercentage(family.getStorageUsagePercentage())
                .createdBy(family.getCreatedBy().getValue())
                .createdAt(family.getCreatedAt())
                .updatedAt(family.getUpdatedAt())
                .build();
    }

    private FamilyMemberResponse toMemberResponse(FamilyMember member, User user) {
        return FamilyMemberResponse.builder()
                .id(member.getId())
                .familyId(member.getFamilyId().getValue())
                .userId(member.getUserId().getValue())
                .userEmail(user != null ? user.getEmail().getValue() : null)
                .userFirstName(user != null ? user.getFirstName() : null)
                .userLastName(user != null ? user.getLastName() : null)
                .role(member.getRole())
                .status(member.getStatus())
                .joinedAt(member.getJoinedAt())
                .leftAt(member.getLeftAt())
                .build();
    }
}
