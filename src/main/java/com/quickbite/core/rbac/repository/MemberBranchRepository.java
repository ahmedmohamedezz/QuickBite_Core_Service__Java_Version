package com.quickbite.core.rbac.repository;

import com.quickbite.core.rbac.domain.MemberBranchEntity;
import com.quickbite.core.rbac.domain.BranchMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberBranchRepository extends JpaRepository<MemberBranchEntity, BranchMemberId> {
    // refresh first-level cache
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM MemberBranchEntity mb WHERE mb.member.id = :memberId")
    void deleteByMemberId(Long memberId);

    default void setMemberBranches(Long memberId, List<MemberBranchEntity> memberBranchEntities) {
        deleteByMemberId(memberId);
        saveAll(memberBranchEntities);
    }

    @Query(value = "SELECT branch_id FROM member_branches WHERE member_id = :memberId",
            nativeQuery = true)
    List<Long> findBranchIdsByMemberId(Long memberId);
}
