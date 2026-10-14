package com.splitgo.api.member;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    boolean existsByGroup_IdAndNameIgnoreCase(UUID groupId, String name);
}