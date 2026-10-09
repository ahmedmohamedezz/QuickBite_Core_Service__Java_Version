package com.quickbite.core.rbac.controller;

import com.quickbite.core.common.annotation.CheckPermission;
import com.quickbite.core.common.annotation.RequireRestaurantMember;
import com.quickbite.core.common.http.ApiResponse;
import com.quickbite.core.rbac.dto.RestaurantMemberCreateDto;
import com.quickbite.core.rbac.dto.RestaurantMemberDto;
import com.quickbite.core.rbac.dto.RoleOrStatusDto;
import com.quickbite.core.rbac.service.MemberService;
import com.quickbite.core.rbac.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PermissionService permissionService;

    @PostMapping("/restaurants/{restaurantId}/members")
    @PreAuthorize("hasAnyRole('system_admin', 'restaurant_user')")
    @RequireRestaurantMember
    @CheckPermission(resource = "core:member", action = "create")
    public ResponseEntity<ApiResponse<RestaurantMemberDto>> createMember(
            @RequestBody RestaurantMemberCreateDto body,
            @PathVariable("restaurantId") Long restaurantId
    ) {
        RestaurantMemberDto result = memberService.createMember(restaurantId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.sendSuccess(result));
    }

    @PutMapping("/restaurants/{restaurantId}/members/{memberId}/branches")
    @PreAuthorize("hasAnyRole('system_admin', 'restaurant_user')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateMemberBranches(
            @RequestBody List<Long> branchIds,
            @PathVariable("restaurantId") Long restaurantId,
            @PathVariable("memberId") Long memberId
    ) {
        Map<String, Object> result = memberService.updateMemberBranches(restaurantId,
                memberId, branchIds);

        return ResponseEntity.ok(ApiResponse.sendSuccess(result));
    }

    @GetMapping("/restaurants/{restaurantId}/members")
    @PreAuthorize("hasAnyRole('system_admin', 'restaurant_user')")
    @RequireRestaurantMember
    @CheckPermission(resource = "core:member", action = "read")
    public ResponseEntity<ApiResponse<List<RestaurantMemberDto>>> getMembers(@PathVariable("restaurantId") Long restaurantId) {
        return ResponseEntity.ok(ApiResponse.sendSuccess(memberService.getRestaurantMembers(restaurantId)));
    }

    @PatchMapping("/restaurants/{restaurantId}/members/{memberId}")
    @PreAuthorize("hasAnyRole('system_admin', 'restaurant_user')")
    @CheckPermission(resource = "core:member", action = "update")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateMemberRoleOrStatus(
            @RequestBody @Valid RoleOrStatusDto body,
            @PathVariable("restaurantId") Long restaurantId,
            @PathVariable("memberId") Long memberId
    ) {
        return ResponseEntity.ok(ApiResponse.sendSuccess(memberService.updateMember(restaurantId, memberId, body)));
    }

    @DeleteMapping("/restaurants/{restaurantId}/members/{memberId}")
    @PreAuthorize("hasAnyRole('system_admin', 'restaurant_user')")
    @CheckPermission(resource = "core:member", action = "delete")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteMember(
            @PathVariable("restaurantId") Long restaurantId,
            @PathVariable("memberId") Long memberId
    ) {
        return ResponseEntity.ok(ApiResponse.sendSuccess(memberService.deleteMember(restaurantId, memberId)));
    }

    @GetMapping("/roles/{role}/permissions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRolePermissions(@PathVariable("role") String role) {
        return ResponseEntity.ok(ApiResponse.sendSuccess(permissionService.getPermissionDetailsByRoleName(role)));
    }
}