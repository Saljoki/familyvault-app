package com.familyvault.infrastructure.persistence.mapper;

import com.familyvault.core.domain.model.family.FamilyId;
import com.familyvault.core.domain.model.file.FileId;
import com.familyvault.core.domain.model.file.StoredFile;
import com.familyvault.core.domain.model.user.UserId;
import com.familyvault.infrastructure.persistence.entity.FileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileMapper {

    @Mapping(target = "id", expression = "java(mapFileId(file.getId()))")
    @Mapping(target = "familyId", expression = "java(mapFamilyId(file.getFamilyId()))")
    @Mapping(target = "folderId", expression = "java(mapFileId(file.getFolderId()))")
    @Mapping(target = "uploadedBy", expression = "java(mapUserId(file.getUploadedBy()))")
    FileEntity toEntity(StoredFile file);

    @Mapping(target = "id", expression = "java(mapToFileId(entity.getId()))")
    @Mapping(target = "familyId", expression = "java(mapToFamilyId(entity.getFamilyId()))")
    @Mapping(target = "folderId", expression = "java(mapToFileId(entity.getFolderId()))")
    @Mapping(target = "uploadedBy", expression = "java(mapToUserId(entity.getUploadedBy()))")
    StoredFile toDomain(FileEntity entity);

    // Helper methods for ID conversion
    default String mapFileId(FileId fileId) {
        return fileId != null ? fileId.getValue() : null;
    }

    default FileId mapToFileId(String id) {
        return id != null ? new FileId(id) : null;
    }

    default String mapFamilyId(FamilyId familyId) {
        return familyId != null ? familyId.getValue() : null;
    }

    default FamilyId mapToFamilyId(String id) {
        return id != null ? new FamilyId(id) : null;
    }

    default String mapUserId(UserId userId) {
        return userId != null ? userId.getValue() : null;
    }

    default UserId mapToUserId(String id) {
        return id != null ? new UserId(id) : null;
    }
}
