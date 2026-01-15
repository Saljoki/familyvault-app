package com.familyvault.core.domain.model.file;

import com.familyvault.core.domain.model.family.FamilyId;
import com.familyvault.core.domain.model.user.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Folder domain entity for organizing files.
 */
@Getter
@Builder(toBuilder = true)
public class Folder {

    private final UUID id;
    private final FamilyId familyId;
    private final UUID parentId; // null for root folders
    private final UserId createdBy;

    private final String name;
    private final String path; // Materialized path: /photos/2024

    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Creates a root folder for a family.
     */
    public static Folder createRoot(FamilyId familyId, UserId createdBy, String name) {
        Instant now = Instant.now();
        return Folder.builder()
                .id(UUID.randomUUID())
                .familyId(familyId)
                .parentId(null)
                .createdBy(createdBy)
                .name(name)
                .path("/" + sanitizeName(name))
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Creates a child folder.
     */
    public static Folder createChild(Folder parent, UserId createdBy, String name) {
        Instant now = Instant.now();
        String childPath = parent.getPath() + "/" + sanitizeName(name);

        return Folder.builder()
                .id(UUID.randomUUID())
                .familyId(parent.getFamilyId())
                .parentId(parent.getId())
                .createdBy(createdBy)
                .name(name)
                .path(childPath)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public Folder rename(String newName) {
        // Note: In a real implementation, you'd need to update child paths too
        String newPath = getParentPath() + "/" + sanitizeName(newName);
        return this.toBuilder()
                .name(newName)
                .path(newPath)
                .updatedAt(Instant.now())
                .build();
    }

    public boolean isRoot() {
        return parentId == null;
    }

    public String getParentPath() {
        if (path == null || path.equals("/")) {
            return "";
        }
        int lastSlash = path.lastIndexOf('/');
        return lastSlash > 0 ? path.substring(0, lastSlash) : "";
    }

    private static String sanitizeName(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
