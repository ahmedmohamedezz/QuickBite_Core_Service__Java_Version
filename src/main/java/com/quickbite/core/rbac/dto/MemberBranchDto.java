package com.quickbite.core.rbac.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MemberBranchDto(
        Long branchId,
        Long memberId,
        LocalDateTime createdAt
) {
}
