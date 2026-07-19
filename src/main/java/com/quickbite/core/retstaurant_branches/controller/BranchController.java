package com.quickbite.core.retstaurant_branches.controller;

import com.quickbite.core.common.security.UserPrincipal;
import com.quickbite.core.retstaurant_branches.dto.*;
import com.quickbite.core.retstaurant_branches.service.BranchService;
import com.quickbite.core.user.enums.SystemRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class BranchController {
    private final BranchService branchService;

    @GetMapping("/branches/nearby")
    public ResponseEntity<List<NearbyBranchProjection>> findNearby(
            @RequestParam("lat") BigDecimal lat,
            @RequestParam("lng") BigDecimal lng
    ) {
        List<NearbyBranchProjection> result = branchService.findNearby(lat, lng);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/restaurants/{restaurantId}/branches")
    public ResponseEntity<Map<String, List<BranchDto>>> findBranches(
            @PathVariable("restaurantId") Long restaurantId
    ) {
        Map<String, List<BranchDto>> result = branchService.findBranches(restaurantId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/restaurants/{restaurantId}/branches")
    @PreAuthorize("hasAnyRole('restaurant_user', 'system_admin')")
    public ResponseEntity<BranchResponse> create(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("restaurantId") Long restaurantId,
            @RequestBody BranchRegisterDto dto
    ) {
        SystemRole userRole = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .anyMatch(auth -> auth.equals("ROLE_system_admin"))
                ? SystemRole.system_admin
                : SystemRole.restaurant_user;

        BranchResponse result = branchService.create(
                restaurantId,
                userPrincipal.getId(),
                userRole,
                dto
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PatchMapping("/branches/{branchId}")
    @PreAuthorize("hasAnyRole('restaurant_user', 'system_admin')")
    public ResponseEntity<BranchResponse> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("branchId") Long branchId,
            @RequestBody @Valid BranchUpdateDto dto
    ) {
        BranchResponse response = branchService.update(principal.getId(), branchId, dto);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/branches/{branchId}/status")
    @PreAuthorize("hasRole('system_admin')")
    public ResponseEntity<BranchResponse> AdminUpdate(
            @PathVariable("branchId") Long branchId,
            @RequestBody @Valid BranchAdminUpdateDto dto
    ) {
        BranchResponse response = branchService.adminUpdate(branchId, dto);
        return ResponseEntity.ok(response);
    }
}
