package com.splitgo.api.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.splitgo.api.payment.Payment;

public record PaymentResponse(UUID id, MemberSummary from, MemberSummary to, BigDecimal amount, Instant createdAt) {

    public record MemberSummary(UUID id, String name) {
    }

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                new MemberSummary(payment.getFromMember().getId(), payment.getFromMember().getName()),
                new MemberSummary(payment.getToMember().getId(), payment.getToMember().getName()),
                payment.getAmount(),
                payment.getCreatedAt());
    }
}