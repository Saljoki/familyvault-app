package com.familyvault.core.domain.model.family;

/**
 * Roles a user can have within a family.
 */
public enum FamilyRole {
    /**
     * Family owner - full control, can delete family
     */
    OWNER,

    /**
     * Administrator - can manage members and content
     */
    ADMIN,

    /**
     * Regular member - can view and upload content
     */
    MEMBER
}
