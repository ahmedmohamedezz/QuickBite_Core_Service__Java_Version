package com.quickbite.core.restaurant.controller;

import com.quickbite.core.common.security.UserPrincipal;
import com.quickbite.core.restaurant.dto.*;
import com.quickbite.core.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping("")
    public ResponseEntity<List<RestaurantDto>> getAll() {
        return ResponseEntity.ok(restaurantService.findAll());
    }

    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDto> getById(@PathVariable("restaurantId") long id) {
        return ResponseEntity.ok(restaurantService.findById(id));
    }

    @PostMapping("")
    @PreAuthorize("hasRole('system_admin')")
    public ResponseEntity<RestaurantResponse> create(@RequestBody RestaurantWithOwnerDto dto) {
        RestaurantResponse response = restaurantService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{restaurantId}")
    @PreAuthorize("hasAnyRole('system_admin', 'restaurant_user')")
    public ResponseEntity<RestaurantResponse> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("restaurantId") Long restaurantId,
            @RequestBody RestaurantUpdateDto dto
    ) {
        RestaurantResponse response = restaurantService.update(principal.getId(), restaurantId, dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{restaurantId}/status")
    @PreAuthorize("hasRole('system_admin')")
    public ResponseEntity<RestaurantResponse> adminUpdate(
            @PathVariable("restaurantId") Long id,
            @RequestBody RestaurantAdminUpdateDto dto
    ) {
        RestaurantResponse response = restaurantService.adminUpdate(id, dto);
        return ResponseEntity.ok(response);
    }
}
