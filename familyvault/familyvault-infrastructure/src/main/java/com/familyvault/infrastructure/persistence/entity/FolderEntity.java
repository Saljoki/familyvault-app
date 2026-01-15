package com.familyvault.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "folders",
       uniqueConstraints = @UniqueConstraint(columnNames = {"family_id", "path"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "family_id", nullable = false, length = 36)
    private String familyId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "parent_id", length = 36)
    private String parentId;

    @Column(name = "path", nullable = false, length = 1000)
    private String path;

    @Column(name = "created_by", nullable = false, length = 36)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "settings", columnDefinition = "jsonb")
    private String settings;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
