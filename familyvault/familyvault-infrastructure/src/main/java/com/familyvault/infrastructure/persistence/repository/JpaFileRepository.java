package com.familyvault.infrastructure.persistence.repository;

import com.familyvault.core.domain.model.file.FileType;
import com.familyvault.infrastructure.persistence.entity.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaFileRepository extends JpaRepository<FileEntity, String> {

    List<FileEntity> findByFamilyId(String familyId);

    List<FileEntity> findByFolderId(String folderId);

    List<FileEntity> findByFamilyIdAndFileType(String familyId, FileType fileType);

    Page<FileEntity> findByFamilyId(String familyId, Pageable pageable);

    Page<FileEntity> findByFamilyIdAndFileType(String familyId, FileType fileType, Pageable pageable);

    Page<FileEntity> findByFolderId(String folderId, Pageable pageable);

    long countByFamilyId(String familyId);

    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM FileEntity f WHERE f.familyId = :familyId")
    Long calculateStorageUsedByFamilyId(@Param("familyId") String familyId);
}
