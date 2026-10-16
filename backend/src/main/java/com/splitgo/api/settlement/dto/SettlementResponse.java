package com.splitgo.api.settlement.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SettlementResponse(
        String currency,
        boolean settled,
        List<MemberBalance> members,
        List<TransferResponse> transfers) {

    public record MemberSummary(UUID id, String name) {
    }

    public record MemberBalance(
            UUID id,
            String name,
            BigDecimal paid,
            BigDecimal consumed,
            BigDecimal sent,
            BigDecimal received,
            BigDecimal balance) {
    }

    public record TransferResponse(MemberSummary from, MemberSummary to, BigDecimal amount) {
    }
}