package com.quickbite.core.product.repository;

import com.quickbite.core.product.domain.ProductBranchDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductBranchDetailsRepository extends JpaRepository<ProductBranchDetailsEntity, Long> {
}
