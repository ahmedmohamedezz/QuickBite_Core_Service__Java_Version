package com.quickbite.core.product.controller;

import com.quickbite.core.common.security.UserPrincipal;
import com.quickbite.core.product.dto.BranchProductDetailsProjection;
import com.quickbite.core.product.dto.category.CategoryDto;
import com.quickbite.core.product.dto.product.ProductDto;
import com.quickbite.core.product.dto.product.ProductRegisterDto;
import com.quickbite.core.product.dto.product.ProductResponse;
import com.quickbite.core.product.dto.product.ProductUpdateDto;
import com.quickbite.core.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/restaurants/{restaurantId}/categories")
    public ResponseEntity<Map<String, List<CategoryDto>>> findCategories(@PathVariable(
            "restaurantId") Long restaurantId) {
        return ResponseEntity.ok(productService.findCategories(restaurantId));
    }

    @GetMapping("/branches/{branchId}/products")
    public ResponseEntity<Map<String, List<BranchProductDetailsProjection>>> findByBranch(@PathVariable("branchId") Long branchId) {
        return ResponseEntity.ok(productService.findByBranch(branchId));
    }

    @GetMapping("/restaurants/{restaurantId}/products")
    @PreAuthorize("hasAnyRole('system_admin', 'restaurant_user')")
    public ResponseEntity<Map<String, List<ProductDto>>> findByRestaurant(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("restaurantId") Long restaurantId
    ) {
        return ResponseEntity.ok(productService.findByRestaurant(principal.getId(), restaurantId));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping("/restaurants/{restaurantId}/products")
    @PreAuthorize("hasAnyRole('system_admin', 'restaurant_user')")
    public ResponseEntity<ProductResponse> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("restaurantId") Long restaurantId,
            @RequestBody @Valid ProductRegisterDto product
    ) {
        ProductResponse response = productService.create(principal.getId(), restaurantId, product);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/products/{id}")
    @PreAuthorize("hasAnyRole('system_admin', 'restaurant_user')")
    public ResponseEntity<ProductResponse> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long productId,
            @RequestBody @Valid ProductUpdateDto product,
            @RequestParam(value = "branchId", required = false) Long branchId
    ) {
        ProductResponse response;
        if (Optional.ofNullable(branchId).isPresent()) {
            response = productService.update(principal.getId(), productId, product, branchId);
        } else {
            response = productService.update(principal.getId(), productId, product, null);
        }

        return ResponseEntity.ok(response);
    }
}
