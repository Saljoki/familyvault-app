package com.familyvault.core.application.dto.response;

import com.familyvault.core.domain.model.family.FamilyRole;
import com.familyvault.core.domain.model.family.FamilyMember.MembershipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberResponse {
    private String id;
    private String familyId;
    private String userId;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private FamilyRole role;
    private MembershipStatus status;
    private Instant joinedAt;
    private Instant leftAt;
}
