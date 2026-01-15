package com.familyvault.core.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyResponse {
    private String id;
    private String name;
    private String description;
    private String familyPicture;
    private String inviteCode;
    private Boolean inviteEnabled;
    private Long storageLimitBytes;
    private Long storageUsedBytes;
    private Double storageUsagePercentage;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;
}
