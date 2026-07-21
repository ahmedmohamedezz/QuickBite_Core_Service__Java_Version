package com.quickbite.core.product.controller;

import com.quickbite.core.product.dto.category.CategoryDto;
import com.quickbite.core.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/restaurants/:restaurantId/categories")
    public ResponseEntity<Map<String, List<CategoryDto>>> findCategories(@RequestParam Long restaurantId) {
        return ResponseEntity.ok(productService.findCategories(restaurantId));
    }
}
