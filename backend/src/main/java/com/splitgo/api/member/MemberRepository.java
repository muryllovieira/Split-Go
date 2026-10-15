package com.splitgo.api.member;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    boolean existsByGroup_IdAndNameIgnoreCase(UUID groupId, String name);

    List<Member> findByGroup_IdOrderByCreatedAtAscNameAsc(UUID groupId);

    Optional<Member> findByIdAndGroup_Id(UUID id, UUID groupId);

    long countByGroup_Id(UUID groupId);

    @Query(value = """
            SELECT EXISTS (SELECT 1 FROM expenses WHERE payer_id = :memberId)
                OR EXISTS (SELECT 1 FROM expense_splits WHERE member_id = :memberId)
                OR EXISTS (SELECT 1 FROM payments WHERE from_member_id = :memberId OR to_member_id = :memberId)
            """, nativeQuery = true)
    boolean hasEntries(@Param("memberId") UUID memberId);
}