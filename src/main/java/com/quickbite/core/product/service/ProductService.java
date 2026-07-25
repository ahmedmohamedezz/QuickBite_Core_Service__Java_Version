package com.quickbite.core.product.service;

import com.quickbite.core.branch.exception.BranchNotFoundException;
import com.quickbite.core.branch.repository.BranchRepository;
import com.quickbite.core.common.exception.impl.UserUnAuthorizedException;
import com.quickbite.core.product.domain.ProductBranchDetailsEntity;
import com.quickbite.core.product.domain.CategoryEntity;
import com.quickbite.core.product.domain.ProductEntity;
import com.quickbite.core.product.dto.BranchProductDetailsProjection;
import com.quickbite.core.product.dto.category.CategoryDto;
import com.quickbite.core.product.dto.product.*;
import com.quickbite.core.product.exception.CategoryNotFoundException;
import com.quickbite.core.product.exception.ProductNotFoundException;
import com.quickbite.core.product.mapper.CategoryMapper;
import com.quickbite.core.product.mapper.ProductBranchMapper;
import com.quickbite.core.product.mapper.ProductMapper;
import com.quickbite.core.product.repository.ProductBranchDetailsRepository;
import com.quickbite.core.product.repository.ProductCategoryRepository;
import com.quickbite.core.product.repository.ProductRepository;
import com.quickbite.core.restaurant.dto.RestaurantDto;
import com.quickbite.core.restaurant.exception.RestaurantNotFoundException;
import com.quickbite.core.restaurant.service.RestaurantService;
import com.quickbite.core.user.dto.UserDto;
import com.quickbite.core.user.enums.SystemRole;
import com.quickbite.core.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final ProductBranchDetailsRepository productBranchDetailsRepository;
    private final ProductBranchMapper productBranchMapper;

    private final ProductCategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    private final BranchRepository branchRepository;

    private final UserService userService;
    private final RestaurantService restaurantService;

    @Transactional(readOnly = true)
    public Map<String, List<CategoryDto>> findCategories(Long restaurantId) {
        // make sure restaurant exists
        if (!restaurantService.existsById(restaurantId)) {
            throw new RestaurantNotFoundException();
        }

        List<CategoryEntity> entities =
                categoryRepository.findByRestaurantId(restaurantId);

        List<CategoryDto> categories = entities.stream().map(categoryMapper::toDto).toList();
        return Map.of("data", categories);
    }

    @Transactional(readOnly = true)
    public Map<String, List<BranchProductDetailsProjection>> findByBranch(Long branchId) {
        List<BranchProductDetailsProjection> data =
                productRepository.findProductsByBranch(branchId);

        return Map.of("data", data);
    }

    @Transactional(readOnly = true)
    public Map<String, List<ProductDto>> findByRestaurant(Long userId, Long restaurantId) {
        UserDto user = userService.getByUserId(userId);
        RestaurantDto restaurant = restaurantService.findById(restaurantId);

        // neither a system admin, nor the restaurant owner
        if (!user.systemRole().equals(SystemRole.system_admin) && !restaurant.ownerId().equals(userId)) {
            throw new UserUnAuthorizedException();
        }

        List<ProductEntity> productEntities = productRepository.findByRestaurantId(restaurantId);
        List<ProductDto> products = productEntities.stream().map(productMapper::toDto).toList();

        return Map.of("data", products);
    }

    @Transactional(readOnly = true)
    public ProductDto findById(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        return productMapper.toDto(productEntity);
    }

    @Transactional
    public ProductResponse create(Long userId, Long restaurantId, ProductRegisterDto product) {
        UserDto user = userService.getByUserId(userId);
        RestaurantDto restaurant = restaurantService.findById(restaurantId);
        ProductEntity newProduct = ProductEntity.builder()
                .name(product.name())
                .description(product.description())
                .imageUrl(product.imageUrl())
                .restaurant(restaurantService.getProxy(restaurantId))
                .build();

        // check ownership
        if (!user.systemRole().equals(SystemRole.system_admin) && !restaurant.ownerId().equals(userId)) {
            throw new UserUnAuthorizedException();
        }

        CategoryEntity categoryEntity = null;
        if (product.categoryName() != null) {
            categoryEntity = categoryRepository
                    .findFirstByRestaurantIdAndName(restaurantId, product.categoryName())
                    .orElse(null);

            if (categoryEntity == null) {
                categoryEntity = new CategoryEntity();
                categoryEntity.setName(product.categoryName());
                categoryEntity.setRestaurant(restaurantService.getProxy(restaurantId));
                categoryRepository.save(categoryEntity);

                newProduct.setCategory(categoryEntity);
            }
        }

        ProductEntity savedProduct = productRepository.save(newProduct);
        return ProductResponse.builder()
                .message("Product created successfully")
                .product(productMapper.toDto(savedProduct))
                .build();
    }

    @Transactional
    public ProductResponse update(
            Long userId,
            Long productId,
            ProductUpdateDto productDto,
            Long branchId   // optional
    ) {
        UserDto user = userService.getByUserId(userId);
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        RestaurantDto restaurant = restaurantService.findByOwnerId(userId);

        // either user is admin, or this product exits in a restaurant owned by this user
        boolean productExistsInRestaurant =
                productBranchDetailsRepository.existsByProductIdAndBranchRestaurantId(productId, restaurant.id());

        if (!user.systemRole().equals(SystemRole.system_admin) && !productExistsInRestaurant) {
            throw new UserUnAuthorizedException();
        }

        // update product details
        productMapper.updateEntityFromDto(productDto, productEntity);

        // update category
        if (productDto.categoryName() != null) {
            CategoryEntity category =
                    categoryRepository.findFirstByRestaurantIdAndName(restaurant.id(),
                            productDto.categoryName()).orElseThrow(CategoryNotFoundException::new);

            productEntity.setCategory(category);
        }

        // update branches-details
        updateProductBranchDetails(productDto, productId, branchId);

        ProductBranchDetailsDto branchDetails = new ProductBranchDetailsDto(
                productDto.price(), productDto.stock(), productDto.isAvailable());

        return ProductResponse.builder()
                .message("Product updated successfully")
                .product(productMapper.toDto(productEntity))
                .branchDetails(branchDetails)
                .build();
    }

    private void updateProductBranchDetails(
            ProductUpdateDto productDto,
            Long productId,
            Long branchId
    ) {
        List<ProductBranchDetailsEntity> productBranchDetails = null;
        if (branchId == null) {
            productBranchDetails = productBranchDetailsRepository.findByProductId(productId);
        } else {
            if (!branchRepository.existsById(branchId)) {
                throw new BranchNotFoundException();
            }

            productBranchDetails =
                    Collections.singletonList(
                            productBranchDetailsRepository
                                    .findFirstByProductIdAndBranchId(productId, branchId)
                    );
        }

        productBranchDetails.forEach(productBranchDetailsEntity -> {
            productBranchMapper.updateEntityFromDto(productDto, productBranchDetailsEntity);
        });
    }
}
