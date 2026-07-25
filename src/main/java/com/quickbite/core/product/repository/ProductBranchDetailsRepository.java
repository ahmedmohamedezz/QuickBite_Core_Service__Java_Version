package com.quickbite.core.product.repository;

import com.quickbite.core.product.domain.ProductBranchDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductBranchDetailsRepository extends JpaRepository<ProductBranchDetailsEntity, Long> {
    boolean existsByProductIdAndBranchRestaurantId(Long productId, Long restaurantId);

    boolean existsByProductIdAndBranchId(Long productId, Long branchId);

    List<ProductBranchDetailsEntity> findByProductId(Long productId);

    ProductBranchDetailsEntity findFirstByProductIdAndBranchId(Long productId, Long branchId);
}
