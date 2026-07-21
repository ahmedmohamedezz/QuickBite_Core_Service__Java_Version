package com.quickbite.core.product.service;

import com.quickbite.core.product.domain.ProductCategoryEntity;
import com.quickbite.core.product.dto.category.CategoryDto;
import com.quickbite.core.product.mapper.CategoryMapper;
import com.quickbite.core.product.repository.ProductCategoryRepository;
import com.quickbite.core.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public Map<String, List<CategoryDto>> findCategories(Long restaurantId) {
        List<ProductCategoryEntity> entities =
                categoryRepository.findByRestaurantId(restaurantId);

        List<CategoryDto> categories = entities.stream().map(categoryMapper::toDto).toList();
        return Map.of("data", categories);
    }
}
