package com.quickbite.core.rbac.dto;

import com.quickbite.core.rbac.domain.RestaurantMemberEntity;
import com.quickbite.core.rbac.domain.RoleEntity;
import com.quickbite.core.rbac.enums.MemberStatus;

public interface MemberWithRoleNameProjection {
    Long getMemberId();

    Long getRestaurantId();

    String getRoleName();
}
