package com.quickbite.core.rbac.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.rbac.enums.MemberStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoleOrStatusDto(
        MemberStatus status,
        String role
) {
}
