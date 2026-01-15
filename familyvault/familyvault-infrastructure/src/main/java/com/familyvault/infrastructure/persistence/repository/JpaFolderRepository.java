package com.familyvault.infrastructure.persistence.repository;

import com.familyvault.infrastructure.persistence.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaFolderRepository extends JpaRepository<FolderEntity, String> {

    List<FolderEntity> findByFamilyId(String familyId);

    List<FolderEntity> findByParentId(String parentId);

    Optional<FolderEntity> findByFamilyIdAndPath(String familyId, String path);
}
