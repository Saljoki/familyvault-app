package com.familyvault.core.domain.model.family;

import com.familyvault.core.domain.model.user.UserId;
import lombok.Builder;
import lombok.Getter;

import java.security.SecureRandom;
import java.time.Instant;

/**
 * Family domain entity representing a family group.
 */
@Getter
@Builder(toBuilder = true)
public class Family {

    private static final String INVITE_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int INVITE_CODE_LENGTH = 8;
    private static final long DEFAULT_STORAGE_LIMIT = 10L * 1024 * 1024 * 1024; // 10GB

    private final FamilyId id;
    private final String name;
    private final String description;
    private final String familyPicture;

    private final String inviteCode;
    private final boolean inviteEnabled;

    private final long storageLimitBytes;
    private final long storageUsedBytes;

    private final UserId createdBy;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Creates a new family.
     */
    public static Family create(String name, String description, UserId createdBy) {
        return Family.builder()
                .id(FamilyId.generate())
                .name(name)
                .description(description)
                .inviteCode(generateInviteCode())
                .inviteEnabled(true)
                .storageLimitBytes(DEFAULT_STORAGE_LIMIT)
                .storageUsedBytes(0)
                .createdBy(createdBy)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private static String generateInviteCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(INVITE_CODE_LENGTH);
        for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
            code.append(INVITE_CODE_CHARS.charAt(random.nextInt(INVITE_CODE_CHARS.length())));
        }
        return code.toString();
    }

    public Family regenerateInviteCode() {
        return this.toBuilder()
                .inviteCode(generateInviteCode())
                .updatedAt(Instant.now())
                .build();
    }

    public Family setInviteEnabled(boolean enabled) {
        return this.toBuilder()
                .inviteEnabled(enabled)
                .updatedAt(Instant.now())
                .build();
    }

    public Family updateDetails(String name, String description, String familyPicture) {
        return this.toBuilder()
                .name(name != null ? name : this.name)
                .description(description != null ? description : this.description)
                .familyPicture(familyPicture != null ? familyPicture : this.familyPicture)
                .updatedAt(Instant.now())
                .build();
    }

    public Family addStorageUsed(long bytes) {
        return this.toBuilder()
                .storageUsedBytes(this.storageUsedBytes + bytes)
                .updatedAt(Instant.now())
                .build();
    }

    public Family removeStorageUsed(long bytes) {
        long newUsed = Math.max(0, this.storageUsedBytes - bytes);
        return this.toBuilder()
                .storageUsedBytes(newUsed)
                .updatedAt(Instant.now())
                .build();
    }

    public boolean hasStorageAvailable(long bytesNeeded) {
        return (storageUsedBytes + bytesNeeded) <= storageLimitBytes;
    }

    public double getStorageUsagePercentage() {
        if (storageLimitBytes == 0) return 0;
        return (double) storageUsedBytes / storageLimitBytes * 100;
    }

    public long getStorageAvailableBytes() {
        return Math.max(0, storageLimitBytes - storageUsedBytes);
    }
}
