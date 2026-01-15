package com.familyvault.api.controller;

import com.familyvault.api.security.CurrentUser;
import com.familyvault.core.application.dto.request.CreateFamilyRequest;
import com.familyvault.core.application.dto.request.JoinFamilyRequest;
import com.familyvault.core.application.dto.request.UpdateMemberRoleRequest;
import com.familyvault.core.application.dto.response.FamilyMemberResponse;
import com.familyvault.core.application.dto.response.FamilyResponse;
import com.familyvault.core.application.service.family.FamilyService;
import com.familyvault.core.domain.model.family.FamilyId;
import com.familyvault.core.domain.model.user.User;
import com.familyvault.core.domain.model.user.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/families")
@RequiredArgsConstructor
@Tag(name = "Family", description = "Family management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class FamilyController {

    private final FamilyService familyService;

    @PostMapping
    @Operation(summary = "Create a new family")
    public ResponseEntity<FamilyResponse> createFamily(
            @Valid @RequestBody CreateFamilyRequest request,
            @CurrentUser User user) {
        FamilyResponse response = familyService.createFamily(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/join")
    @Operation(summary = "Join a family using invite code")
    public ResponseEntity<FamilyResponse> joinFamily(
            @Valid @RequestBody JoinFamilyRequest request,
            @CurrentUser User user) {
        FamilyResponse response = familyService.joinFamily(request, user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List all families the user is a member of")
    public ResponseEntity<List<FamilyResponse>> listFamilies(@CurrentUser User user) {
        List<FamilyResponse> families = familyService.listFamilies(user.getId());
        return ResponseEntity.ok(families);
    }

    @GetMapping("/{familyId}")
    @Operation(summary = "Get family details")
    public ResponseEntity<FamilyResponse> getFamily(
            @PathVariable String familyId,
            @CurrentUser User user) {
        FamilyResponse response = familyService.getFamily(new FamilyId(familyId), user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{familyId}/members")
    @Operation(summary = "List all members of a family")
    public ResponseEntity<List<FamilyMemberResponse>> listMembers(
            @PathVariable String familyId,
            @CurrentUser User user) {
        List<FamilyMemberResponse> members = familyService.listMembers(new FamilyId(familyId), user.getId());
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{familyId}/invite/regenerate")
    @Operation(summary = "Regenerate family invite code")
    public ResponseEntity<FamilyResponse> regenerateInviteCode(
            @PathVariable String familyId,
            @CurrentUser User user) {
        FamilyResponse response = familyService.regenerateInviteCode(new FamilyId(familyId), user.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{familyId}/invite/toggle")
    @Operation(summary = "Enable or disable family invites")
    public ResponseEntity<FamilyResponse> toggleInvite(
            @PathVariable String familyId,
            @RequestParam boolean enabled,
            @CurrentUser User user) {
        FamilyResponse response = familyService.toggleInvite(new FamilyId(familyId), user.getId(), enabled);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{familyId}/members/{userId}/role")
    @Operation(summary = "Update member role")
    public ResponseEntity<Void> updateMemberRole(
            @PathVariable String familyId,
            @PathVariable String userId,
            @Valid @RequestBody UpdateMemberRoleRequest request,
            @CurrentUser User currentUser) {
        familyService.updateMemberRole(
                new FamilyId(familyId),
                new UserId(userId),
                request.getRole(),
                currentUser.getId()
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{familyId}/members/{userId}")
    @Operation(summary = "Remove member from family")
    public ResponseEntity<Void> removeMember(
            @PathVariable String familyId,
            @PathVariable String userId,
            @CurrentUser User currentUser) {
        familyService.removeMember(
                new FamilyId(familyId),
                new UserId(userId),
                currentUser.getId()
        );
        return ResponseEntity.noContent().build();
    }
}
