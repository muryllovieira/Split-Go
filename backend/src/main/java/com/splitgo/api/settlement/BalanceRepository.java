package com.splitgo.api.settlement;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.splitgo.api.member.Member;

public interface BalanceRepository extends Repository<Member, UUID> {

    @Query(value = """
            SELECT m.id AS memberId,
                   m.name AS name,
                   COALESCE((SELECT SUM(e.amount) FROM expenses e WHERE e.payer_id = m.id), 0) AS paid,
                   COALESCE((SELECT SUM(s.amount) FROM expense_splits s WHERE s.member_id = m.id), 0) AS consumed,
                   COALESCE((SELECT SUM(p.amount) FROM payments p WHERE p.from_member_id = m.id), 0) AS sent,
                   COALESCE((SELECT SUM(p.amount) FROM payments p WHERE p.to_member_id = m.id), 0) AS received
            FROM members m
            WHERE m.group_id = :groupId
            ORDER BY m.created_at, m.name
            """, nativeQuery = true)
    List<MemberBalanceView> findBalancesByGroupId(@Param("groupId") UUID groupId);
}