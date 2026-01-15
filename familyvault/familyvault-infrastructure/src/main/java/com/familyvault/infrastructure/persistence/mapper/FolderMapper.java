package com.familyvault.infrastructure.persistence.mapper;

import com.familyvault.core.domain.model.family.FamilyId;
import com.familyvault.core.domain.model.file.FileId;
import com.familyvault.core.domain.model.file.Folder;
import com.familyvault.core.domain.model.user.UserId;
import com.familyvault.infrastructure.persistence.entity.FolderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FolderMapper {

    @Mapping(target = "id", expression = "java(mapFileId(folder.getId()))")
    @Mapping(target = "familyId", expression = "java(mapFamilyId(folder.getFamilyId()))")
    @Mapping(target = "parentId", expression = "java(mapFileId(folder.getParentId()))")
    @Mapping(target = "createdBy", expression = "java(mapUserId(folder.getCreatedBy()))")
    FolderEntity toEntity(Folder folder);

    @Mapping(target = "id", expression = "java(mapToFileId(entity.getId()))")
    @Mapping(target = "familyId", expression = "java(mapToFamilyId(entity.getFamilyId()))")
    @Mapping(target = "parentId", expression = "java(mapToFileId(entity.getParentId()))")
    @Mapping(target = "createdBy", expression = "java(mapToUserId(entity.getCreatedBy()))")
    Folder toDomain(FolderEntity entity);

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
