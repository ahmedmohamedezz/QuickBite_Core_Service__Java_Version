package com.quickbite.core.retstaurant_branches.repository;

import com.quickbite.core.retstaurant_branches.domain.BranchEntity;
import com.quickbite.core.retstaurant_branches.dto.BranchDto;
import com.quickbite.core.retstaurant_branches.dto.NearbyBranchProjection;
import com.quickbite.core.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, Long> {
    @Query(value = """
              SELECT 
                b.id,
                b.restaurant_id AS restaurantId,
                b.address_text AS addressText,
                b.label,
                b.lat,
                b.lng,
                b.is_active AS isActive,
                b.accept_orders AS acceptOrders,
                b.currency,
                r.name,
                r.logo_url AS logoUrl
              FROM restaurant_branches b 
              JOIN restaurants r ON b.restaurant_id = r.id
              WHERE b.is_active = true 
                AND r.status = 'active'
                AND ST_DWithin(b.location, ST_MakePoint(:lng, :lat)::geography, b.delivery_radius * 1000)
            """, nativeQuery = true)
    List<NearbyBranchProjection> findNearbyBranches(
            @Param("lat") BigDecimal lat,
            @Param("lng") BigDecimal lng
    );

    @Query(value = "SELECT b FROM BranchEntity b where b.restaurant.id = :restaurantId")
    List<BranchEntity> findBranches(Long restaurantId);

    @Query(value = """
            SELECT u.* FROM users WHERE u.id = (
                        SELECT r.ownerId
                        FROM restaurants r JOIN restaurant_branches b
                        ON b.restaurantId = r.id AND r.id = :branchId
                    )
            """, nativeQuery = true)
    UserEntity getOwner(Long branchId);
}
