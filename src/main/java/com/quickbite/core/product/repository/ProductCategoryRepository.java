package com.quickbite.core.product.repository;

import com.quickbite.core.product.domain.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findByRestaurantId(Long restaurantId);

    Optional<CategoryEntity> findFirstByRestaurantIdAndName(Long restaurantId, String name);
}
