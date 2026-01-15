package com.familyvault.core.application.port.out.persistence;

import com.familyvault.core.domain.model.family.FamilyId;
import com.familyvault.core.domain.model.file.FileId;
import com.familyvault.core.domain.model.file.FileType;
import com.familyvault.core.domain.model.file.Folder;
import com.familyvault.core.domain.model.file.StoredFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port for file and folder persistence operations.
 */
public interface FileRepository {

    // File operations
    StoredFile saveFile(StoredFile file);

    Optional<StoredFile> findFileById(FileId id);

    List<StoredFile> findFilesByFamily(FamilyId familyId);

    List<StoredFile> findFilesByFolder(FamilyId familyId, UUID folderId);

    List<StoredFile> findFilesByType(FamilyId familyId, FileType type);

    /**
     * Find files with pagination.
     */
    List<StoredFile> findFiles(FamilyId familyId, UUID folderId, FileType type, int page, int size);

    /**
     * Count files in a family.
     */
    long countFilesByFamily(FamilyId familyId);

    /**
     * Calculate total storage used by a family.
     */
    long calculateStorageUsed(FamilyId familyId);

    void deleteFile(FileId id);

    // Folder operations
    Folder saveFolder(Folder folder);

    Optional<Folder> findFolderById(UUID id);

    List<Folder> findFoldersByFamily(FamilyId familyId);

    List<Folder> findFoldersByParent(FamilyId familyId, UUID parentId);

    Optional<Folder> findFolderByPath(FamilyId familyId, String path);

    void deleteFolder(UUID id);
}
