package com.familyvault.infrastructure.persistence.adapter;

import com.familyvault.core.application.port.out.persistence.FileRepository;
import com.familyvault.core.domain.model.family.FamilyId;
import com.familyvault.core.domain.model.file.FileId;
import com.familyvault.core.domain.model.file.FileType;
import com.familyvault.core.domain.model.file.Folder;
import com.familyvault.core.domain.model.file.StoredFile;
import com.familyvault.infrastructure.persistence.entity.FileEntity;
import com.familyvault.infrastructure.persistence.entity.FolderEntity;
import com.familyvault.infrastructure.persistence.mapper.FileMapper;
import com.familyvault.infrastructure.persistence.mapper.FolderMapper;
import com.familyvault.infrastructure.persistence.repository.JpaFileRepository;
import com.familyvault.infrastructure.persistence.repository.JpaFolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileRepositoryAdapter implements FileRepository {

    private final JpaFileRepository jpaFileRepository;
    private final JpaFolderRepository jpaFolderRepository;
    private final FileMapper fileMapper;
    private final FolderMapper folderMapper;

    @Override
    @Transactional
    public StoredFile saveFile(StoredFile file) {
        FileEntity entity = fileMapper.toEntity(file);
        FileEntity saved = jpaFileRepository.save(entity);
        return fileMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StoredFile> findFileById(FileId id) {
        return jpaFileRepository.findById(id.getValue())
                .map(fileMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoredFile> findFilesByFamily(FamilyId familyId) {
        return jpaFileRepository.findByFamilyId(familyId.getValue())
                .stream()
                .map(fileMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoredFile> findFilesByFolder(FileId folderId) {
        return jpaFileRepository.findByFolderId(folderId.getValue())
                .stream()
                .map(fileMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoredFile> findFilesByType(FamilyId familyId, FileType fileType) {
        return jpaFileRepository.findByFamilyIdAndFileType(familyId.getValue(), fileType)
                .stream()
                .map(fileMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoredFile> findFiles(FamilyId familyId, FileId folderId, FileType fileType, Pageable pageable) {
        Page<FileEntity> entities;

        if (folderId != null) {
            entities = jpaFileRepository.findByFolderId(folderId.getValue(), pageable);
        } else if (fileType != null) {
            entities = jpaFileRepository.findByFamilyIdAndFileType(familyId.getValue(), fileType, pageable);
        } else {
            entities = jpaFileRepository.findByFamilyId(familyId.getValue(), pageable);
        }

        return entities.map(fileMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public long countFilesByFamily(FamilyId familyId) {
        return jpaFileRepository.countByFamilyId(familyId.getValue());
    }

    @Override
    @Transactional(readOnly = true)
    public long calculateStorageUsed(FamilyId familyId) {
        Long storageUsed = jpaFileRepository.calculateStorageUsedByFamilyId(familyId.getValue());
        return storageUsed != null ? storageUsed : 0L;
    }

    @Override
    @Transactional
    public void deleteFile(FileId id) {
        jpaFileRepository.deleteById(id.getValue());
    }

    @Override
    @Transactional
    public Folder saveFolder(Folder folder) {
        FolderEntity entity = folderMapper.toEntity(folder);
        FolderEntity saved = jpaFolderRepository.save(entity);
        return folderMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Folder> findFolderById(FileId id) {
        return jpaFolderRepository.findById(id.getValue())
                .map(folderMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Folder> findFoldersByFamily(FamilyId familyId) {
        return jpaFolderRepository.findByFamilyId(familyId.getValue())
                .stream()
                .map(folderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Folder> findFoldersByParent(FileId parentId) {
        return jpaFolderRepository.findByParentId(parentId.getValue())
                .stream()
                .map(folderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Folder> findFolderByPath(FamilyId familyId, String path) {
        return jpaFolderRepository.findByFamilyIdAndPath(familyId.getValue(), path)
                .map(folderMapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteFolder(FileId id) {
        jpaFolderRepository.deleteById(id.getValue());
    }
}
