package com.splitgo.api.group.dto;

import java.time.Instant;
import java.util.UUID;

import com.splitgo.api.group.Group;

public record GroupResponse(UUID id, String name, String baseCurrency, Instant createdAt) {

    public static GroupResponse from(Group group) {
        return new GroupResponse(group.getId(), group.getName(), group.getBaseCurrency(), group.getCreatedAt());
    }
}