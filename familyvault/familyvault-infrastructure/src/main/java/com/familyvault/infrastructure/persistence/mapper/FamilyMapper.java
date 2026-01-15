package com.familyvault.infrastructure.persistence.mapper;

import com.familyvault.core.domain.model.family.Family;
import com.familyvault.core.domain.model.family.FamilyId;
import com.familyvault.core.domain.model.family.FamilyMember;
import com.familyvault.core.domain.model.user.UserId;
import com.familyvault.infrastructure.persistence.entity.FamilyEntity;
import com.familyvault.infrastructure.persistence.entity.FamilyMemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FamilyMapper {

    @Mapping(target = "id", expression = "java(mapFamilyId(family.getId()))")
    @Mapping(target = "createdBy", expression = "java(mapUserId(family.getCreatedBy()))")
    FamilyEntity toEntity(Family family);

    @Mapping(target = "id", expression = "java(mapToFamilyId(entity.getId()))")
    @Mapping(target = "createdBy", expression = "java(mapToUserId(entity.getCreatedBy()))")
    Family toDomain(FamilyEntity entity);

    @Mapping(target = "id", expression = "java(member.getId())")
    @Mapping(target = "familyId", expression = "java(mapFamilyId(member.getFamilyId()))")
    @Mapping(target = "userId", expression = "java(mapUserId(member.getUserId()))")
    FamilyMemberEntity toEntity(FamilyMember member);

    @Mapping(target = "id", expression = "java(entity.getId())")
    @Mapping(target = "familyId", expression = "java(mapToFamilyId(entity.getFamilyId()))")
    @Mapping(target = "userId", expression = "java(mapToUserId(entity.getUserId()))")
    FamilyMember toDomain(FamilyMemberEntity entity);

    // Helper methods for ID conversion
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
