package com.splitgo.api.member.dto;

import java.time.Instant;
import java.util.UUID;

import com.splitgo.api.member.Member;

public record MemberResponse(UUID id, String name, Instant createdAt) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getName(), member.getCreatedAt());
    }
}