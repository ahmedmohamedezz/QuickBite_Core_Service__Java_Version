package com.quickbite.core.rbac.repository;

import com.quickbite.core.rbac.domain.RestaurantMemberEntity;
import com.quickbite.core.rbac.dto.MemberWithRoleNameProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantMemberRepository extends JpaRepository<RestaurantMemberEntity, Long> {
    @Query(value = """
                UPDATE RestaurantMemberEntity entity
                SET entity.status = 'active', entity.updatedAt = CURRENT_TIMESTAMP
                WHERE entity.user.id = :userId
            """)
    @Modifying
    void activateMemberByUserId(Long userId);

    @Query(value = """
                    SELECT rm.id as memberId, rm.restaurant_id as restaurantId, r.name as roleName
                    FROM restaurant_members rm LEFT JOIN roles r
                    ON rm.role_id = r.id
                    WHERE rm.user_id = :userId AND rm.status = 'active'
            """, nativeQuery = true)
    Optional<MemberWithRoleNameProjection> findMemberWithRoleName(Long userId);

    List<RestaurantMemberEntity> findByRestaurantId(Long restaurantId);

    @Modifying
    void deleteByIdAndRestaurantId(Long memberId, Long restaurantId);
}
