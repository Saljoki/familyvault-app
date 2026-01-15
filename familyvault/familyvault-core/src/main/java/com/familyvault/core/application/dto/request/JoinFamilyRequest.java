package com.familyvault.core.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinFamilyRequest {

    @NotBlank(message = "Invite code is required")
    private String inviteCode;
}
