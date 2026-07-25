package com.quickbite.core.product.repository;

import com.quickbite.core.product.domain.ProductEntity;
import com.quickbite.core.product.dto.BranchProductDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    @Query(value = """
                SELECT p.id, p.name, p.description, p.image_url, p.restaurant_id, p.cateogry_id,
                       c.name AS category_name,
                       pd.price, pd.stock, pd.is_available
                FROM product p
                    JOIN product_categories c ON p.category_id = c.id
                    JOIN product_branch_details pd ON p.id = pd.product_id
                WHERE pd.branch_id = :branchId
            """, nativeQuery = true)
    List<BranchProductDetailsProjection> findProductsByBranch(Long branchId);

    List<ProductEntity> findByRestaurantId(Long restaurantId);
}
