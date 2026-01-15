package com.familyvault.core.application.dto.request;

import com.familyvault.core.domain.model.family.FamilyRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMemberRoleRequest {

    @NotNull(message = "Role is required")
    private FamilyRole role;
}
