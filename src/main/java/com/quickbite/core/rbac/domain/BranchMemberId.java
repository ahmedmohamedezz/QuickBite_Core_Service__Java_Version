package com.quickbite.core.rbac.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Builder
@Embeddable
public class BranchMemberId implements Serializable {
    @Column(name = "branch_id")
    private Long branchId;

    @Column(name = "member_id")
    private Long memberId;

    public BranchMemberId(Long branchId, Long memberId) {
        this.branchId = branchId;
        this.memberId = memberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        BranchMemberId that = (BranchMemberId) o;
        return Objects.equals(memberId, that.memberId) &&
                Objects.equals(branchId, that.branchId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, branchId);
    }
}
