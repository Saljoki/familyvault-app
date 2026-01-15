package com.familyvault.infrastructure.persistence.entity;

import com.familyvault.core.domain.model.family.FamilyRole;
import com.familyvault.core.domain.model.family.FamilyMember.MembershipStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "family_members",
       uniqueConstraints = @UniqueConstraint(columnNames = {"family_id", "user_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "family_id", nullable = false, length = 36)
    private String familyId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private FamilyRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MembershipStatus status;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    @Column(name = "left_at")
    private Instant leftAt;

    @PrePersist
    protected void onCreate() {
        if (joinedAt == null) {
            joinedAt = Instant.now();
        }
    }
}
